//: com.yulikexuan.ssl.app.events.OnRegistrationCompleteEventListener.java


package com.yulikexuan.ssl.app.events;


import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.model.VerificationToken;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class OnRegistrationCompleteEventListener
        implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final IUserService userService;
    private final JavaMailSender mailSender;
    private final Environment env;

    @Autowired
    public OnRegistrationCompleteEventListener(IUserService userService,
                                               JavaMailSender mailSender,
                                               Environment env) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {

        final VerificationToken verificationToken = event.getVerificationToken();
        final User user = verificationToken.getUser();

        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";

        final SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to verify your account: \r\n"
                + event.getAppUri());
        email.setFrom("yulikexuan@gmail.com");

        this.mailSender.send(email);
    }

}///:~
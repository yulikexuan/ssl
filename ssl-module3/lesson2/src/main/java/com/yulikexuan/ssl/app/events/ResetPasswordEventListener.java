//: com.yulikexuan.ssl.app.events.ResetPasswordEventListener.java


package com.yulikexuan.ssl.app.events;


import com.yulikexuan.ssl.domain.model.PasswordResetToken;
import com.yulikexuan.ssl.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class ResetPasswordEventListener implements ApplicationListener<ResetPasswordEvent> {

    private final JavaMailSender mailSender;

    @Autowired
    public ResetPasswordEventListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(ResetPasswordEvent event) {

        final PasswordResetToken passwordResetToken = event.getPasswordResetToken();

        final User user = passwordResetToken.getUser();
        final String recipientAddress = user.getEmail();
        final String subject = "Reset Password";
        final SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to reset your password: \r\n"
                + event.getUri());
        email.setFrom("yulikexuan@gmail.com");

        this.mailSender.send(email);
    }

}///:~
//: com.yulikexuan.ssl.app.events.OnRegistrationCompleteEvent.java


package com.yulikexuan.ssl.app.events;


import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.model.VerificationToken;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUri;
    private final VerificationToken verificationToken;

    public OnRegistrationCompleteEvent(VerificationToken verificationToken,
                                       String appUri) {
        super(verificationToken);
        this.appUri = appUri;
        this.verificationToken = verificationToken;
    }

}///:~
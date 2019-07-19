//: com.yulikexuan.ssl.app.events.ResetPasswordEvent.java


package com.yulikexuan.ssl.app.events;


import com.yulikexuan.ssl.domain.model.PasswordResetToken;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class ResetPasswordEvent extends ApplicationEvent {

    private final PasswordResetToken passwordResetToken;
    private final String uri;

    public ResetPasswordEvent(PasswordResetToken passwordResetToken, String uri) {

        super(passwordResetToken);
        this.uri = uri;
        this.passwordResetToken = passwordResetToken;
    }

}///:~
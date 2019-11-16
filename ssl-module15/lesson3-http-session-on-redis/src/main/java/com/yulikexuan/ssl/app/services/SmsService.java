//: com.yulikexuan.ssl.app.services.SmsServices.java


package com.yulikexuan.ssl.app.services;


import com.twilio.sdk.client.TwilioRestClient;
import com.twilio.sdk.resource.api.v2010.account.Message;
import com.twilio.sdk.type.PhoneNumber;
import com.yulikexuan.ssl.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SmsService {

    @Value("${twilio.sid:}")
    private String smsAccountSid;

    @Value("${twilio.token:}")
    private String smsToken;

    @Value("${twilio.sender:}")
    private String smsSenderNumber;

    private TwilioRestClient twilioRestClient;

    @Bean
    public TwilioRestClient twilioRestClient() {
        if (this.twilioRestClient == null) {
            this.twilioRestClient = new TwilioRestClient(smsAccountSid, smsToken);
        }

        return this.twilioRestClient;
    }

    public void sendVerificationCode(User user) {

        if (user == null) {
            logTargetError("a null user");
            return;
        }

        String code = new Totp(user.getSecret()).now();
        sendSms(user.getPhone(), "The verification code is " + code);
    }

    public void sendSms(String phoneNumber, String msg) {

        if (phoneNumber == null) {
            this.logTargetError("a null phone number");
        }

        Message message = Message.create(this.smsAccountSid,
                        new PhoneNumber(phoneNumber),
                        new PhoneNumber(this.smsSenderNumber),
                        msg == null ? "Test" : msg)
                .execute(this.twilioRestClient);

        log.info(">>>>>>> Twilio Status: {}", message.getStatus().toString());
    }

    private void logTargetError(String target) {
        log.error(">>>>>>> Cannot send sms to {}!", target);
    }

}///:~
//: com.yulikexuan.ssl.app.config.security.SslPasswordEncoderFactories.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;


public class SslPasswordEncoderFactories {

    private SslPasswordEncoderFactories() {}

    public static PasswordEncoder createDelegatingPasswordEncoder() {

        String defaultEncodingId = "bcrypt";
        String encodingId = "bcrypt12";

        Map<String, PasswordEncoder> encoders = new HashMap<>();

        encoders.put(encodingId, new BCryptPasswordEncoder(12));
        encoders.put(defaultEncodingId, new BCryptPasswordEncoder());

        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());

        DelegatingPasswordEncoder delegatingPasswordEncoder =
                new DelegatingPasswordEncoder(encodingId, encoders);

        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(
                encoders.get(defaultEncodingId));

        return delegatingPasswordEncoder;
    }

}///:~
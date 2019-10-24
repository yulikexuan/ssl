//: com.yulikexuan.ssl.app.config.security.SslAuthorizationServerConfigurationIT.java


package com.yulikexuan.ssl.app.config.security;


import com.yulikexuan.ssl.app.bootstrap.DefaultLoader;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;


/* JWT Sample
 * {
        "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJQUklWSUxFR0VfUkVBRCIsIlBSSVZJTEVHRV9XUklURSJdLCJvcmdhbml6YXRpb24iOiJzc2xDbGllbnRtZ29pIiwiZXhwIjoxNTcxNzUxNzU3LCJqdGkiOiJmY2IyYjFhOC0zOTQ3LTRiMDEtOTU3My0wNzQ0NDhjMTdlM2EiLCJjbGllbnRfaWQiOiJzc2xDbGllbnQifQ.URvtdF_5wyOf9lZXm999Ko5v3UcX7qf8HcsSwFAT2hugGNKOeqsdj64CCfk2E_FnkkqisjnfcH7srD5oyjd9q7h2c6nPjPJWp-BHMLvxUJS6fOWP8Rh1CmeSp5hbVzENXLdEV1fkRFFJj07nLlAmNEEIUogxY-V0KQJAj6G08aFavt5mFIfLIGtaGiyig4M8E5CYQhiv1CB7P0lbptFtultryjogftTFrpOhW14aFgCv9WAJCfXzAN2hSe2GWoXjNHmhlb_N6yMq-qKy7j83VZaP4spi4T8_gqnIbqqPlUcT2y0Du-Jkd6c_f6bvnZStmEgE24MyeHIelocQiifYpw",
        "token_type": "bearer",
        "expires_in": 3599,
        "scope": "PRIVILEGE_READ PRIVILEGE_WRITE",
        "organization": "sslClientmgoi",
        "jti": "fcb2b1a8-3947-4b01-9573-074448c17e3a"
    }
 *
 * How to define test properties in yaml configuration file:
 *   1.  Create a yaml file in folder: test/resources
 *   2.  Name it "application-test.yml"
 *   3.  Use Annotation: @ActiveProfiles("test") for each test java file
 *   4.  All properties defined in application.yml are still going to be applied
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SslAuthorizationServerConfigurationIT {

    @Value("${ssl.oauth2.server.root}")
    private String oauthServerRoot;

    @Value("${local.server.port}")
    private String port;

    @Value("${ssl.oauth2.jwt.token.uri}")
    private String jwtTokenUri;

    @Value("${ssl.oauth2.jwt.token.param.name_grant_type}")
    private String grantTypeParamName;

    @Value("${ssl.oauth2.jwt.token.param.value_grant_type}")
    private String grantTypeParamValue;

    private String tokenUrl;

    @BeforeEach
    void setUp() {
        this.tokenUrl = String.format(this.oauthServerRoot, this.port)
                + this.jwtTokenUri;
    }

    @DisplayName("Test Getting JWT Token - ")
    @Test
    void able_To_Get_JWT_Token() {

        // Given

        // When
        final Response response = given().auth().preemptive()
                .basic("sslClient", DefaultLoader.CLIENT_SECRET)
                .with()
                .formParam(this.grantTypeParamName, this.grantTypeParamValue)
                .post(this.tokenUrl);

        // Then
        Assertions.assertAll(
                "Getting JWT Token failed.",
                () -> assertThat(response.getStatusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath()
                        .getString("access_token"))
                        .isNotNull()
        );

    }

}///:~
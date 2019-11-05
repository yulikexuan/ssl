//: com.yulikexuan.ssl.app.config.security.SslAuthorizationServerConfigurationIT.java


package com.yulikexuan.ssl.app.config.security;


import com.jayway.restassured.response.Response;
import com.yulikexuan.ssl.app.bootstrap.DefaultLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


/* JWT Sample
 * {
        "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6Wy",
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
@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("OAuth2 Authorization Server Test - ")
class SslAuthorizationServerConfigurationIT {

    static final String SSL_PASSWORD_CLIENT_ID = "cloud";
    static final String SSL_CLIENT_ID = "sslClient";
    static final String SSL_READ_ONLY_CLIENT_ID = "sslReadOnlyClient";

    @Value("${ssl.user.name}")
    private String sslUserName;

    @Value("${ssl.user.password}")
    private String sslUserPassword;

    @Value("${ssl.oauth2.jwt.token.param.name_username}")
    private String paramNameUsername;

    @Value("${ssl.oauth2.jwt.token.param.name_password}")
    private String paramNamePassword;

    @Value("${ssl.oauth2.server.root}")
    private String oauthServerRoot;

    @Value("${local.server.port}")
    private String port;

    @Value("${ssl.oauth2.jwt.token.uri}")
    private String jwtTokenUri;

    @Value("${ssl.oauth2.jwt.token.node.name}")
    private String jwtTokenNodeName;

    @Value("${ssl.oauth2.jwt.token.node.refresh}")
    private String jwtRefreshTokenNodeName;

    @Value("${ssl.oauth2.jwt.token.param.name_grant_type}")
    private String grantTypeParamName;

    @Value("${ssl.oauth2.jwt.token.param.cc_grant_type}")
    private String clientCredentialGrantTypeParamValue;

    @Value("${ssl.oauth2.jwt.token.param.pw_grant_type}")
    private String passwordGrantTypeParamValue;

    private String tokenRequestUrl;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        this.baseUrl = String.format(this.oauthServerRoot, this.port);
        this.tokenRequestUrl = this.baseUrl + this.jwtTokenUri;
    }

    @DisplayName("JWT Token Test for different Grant Types - ")
    @Nested
    class JwtTokenTest {

        @DisplayName("Test JWT Token with Client Credential Grant Type - ")
        // @RepeatedTest(value = 2, name = "{displayName} : {currentRepetition} / {totalRepetitions}")
        @Test
        void able_To_Get_JWT_Token_For_Client_Credential_Grant_Type() {

            // When
            given().auth()
                    /*
                     * preemptive(): Returns the preemptive authentication view.
                     * This means that the authentication details are sent in the
                     * request header regardless if the server has challenged for
                     * authentication or not
                     */
                    .preemptive()
                    .basic(SSL_CLIENT_ID, DefaultLoader.CLIENT_SECRET)
                    .with()
                    .formParam(grantTypeParamName, clientCredentialGrantTypeParamValue)
                    .when()
                    .post(tokenRequestUrl)
                    .then()
                    .log()
                    .ifValidationFails()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .and()
                    .body(jwtTokenNodeName, notNullValue())
                    .and()
                    .body(jwtRefreshTokenNodeName, notNullValue())
                    .and()
                    .time(lessThan(2000L));
        }

        @DisplayName("Test JWT Token with Password Grant Type - ")
        @Test
        void able_To_Get_JWT_For_Password_Client() {

            given().auth()
                    /*
                     * preemptive(): Returns the preemptive authentication view.
                     * This means that the authentication details are sent in the
                     * request header regardless if the server has challenged for
                     * authentication or not
                     */
                    .preemptive()
                    .basic(SSL_PASSWORD_CLIENT_ID, DefaultLoader.CLIENT_SECRET)
                    .with()
                    .param(grantTypeParamName, passwordGrantTypeParamValue)
                    .with()
                    .formParam(paramNameUsername, sslUserName)
                    .with()
                    .formParam(paramNamePassword, sslUserPassword)
                    .when()
                    .post(tokenRequestUrl)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(HttpStatus.SC_OK)
                    .body(jwtTokenNodeName, notNullValue())
                    .and()
                    .time(lessThan(2000L));
        }

    }//: End of class JwtTokenTest

    @DisplayName("Read & Write Scopes Test - ")
    @Nested
    class GenericClientTest {

        private String accessToken;

        @BeforeEach
        void setUp() {
            accessToken = String.format("Bearer %1$s",
                    getAccessToken(SSL_CLIENT_ID, DefaultLoader.CLIENT_SECRET));
        }

        @DisplayName("Client with R/W Scopes Should be able to Read - ")
        @Test
        void test_Client_Read_With_Read_And_Write_Scropes() {

            given().header("Authorization", this.accessToken)
                    .when()
                    .get(baseUrl + "/api/users/me")
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(HttpStatus.SC_OK)
                    .body("principal", equalTo(SSL_CLIENT_ID));
        }

        @DisplayName("Client with R/W Scopes Should be able to Create New User - ")
        @Test
        void test_Client_Write_With_Read_And_Write_Scopes() {

            // Given
            final Map<String, String> params = getUserParams();

            given().header("Authorization", this.accessToken)
                    .formParameters(params)
                    .when()
                    .post(baseUrl + "/api/users")
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(HttpStatus.SC_CREATED);
        }

    }//: End of class GenericClientTest

    @DisplayName("Read Only Client Test - ")
    @Nested
    class ReadOnlyClientTest {

        private String accessToken;

        @BeforeEach
        void setUp() {
            accessToken = String.format("Bearer %1$s",
                    getAccessToken(SSL_READ_ONLY_CLIENT_ID,
                            DefaultLoader.CLIENT_SECRET));
        }

        @DisplayName("Client with Only Read Scope Should be able to Read - ")
        @Test
        void test_Client_Read_With_Only_Read_Scope() {

            given().header("Authorization", this.accessToken)
                    .when()
                    .get(baseUrl + "/api/users/me")
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(HttpStatus.SC_OK)
                    .body("principal", equalTo(SSL_READ_ONLY_CLIENT_ID));
        }

        @DisplayName("Client with Only Read Scope Should NOT be able to Write or Create - ")
        @Test
        void test_Client_Write_With_Only_Read_Scope() {

            // Given
            final Map<String, String> params = getUserParams();

            // When
            given().header("Authorization", this.accessToken)
                    .formParameters(params)
                    .when()
                    .post(baseUrl + "/api/users")
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(HttpStatus.SC_FORBIDDEN);
        }

    }//: End of ReadOnlyClilentTest

    @DisplayName("Jayway Restassured Test - ")
    @Nested
    class RestassuredTest {

        String accessToken;

        @BeforeEach
        void setUp() {
            accessToken = String.format("Bearer %1$s",
                    getAccessToken(SSL_CLIENT_ID, DefaultLoader.CLIENT_SECRET));
        }

        @DisplayName("Test anonymous json root - ")
        @Test
        void test_Anonymous_Json_Root() {

            // When
            given().header("Authorization", accessToken)
                    .when()
                    .get(baseUrl + "/api/restassured/anonymous")
                    .then()
                    .log()
                    .ifValidationFails()
                    .assertThat()
                    .body("$", hasItems(1, 2, 3))
                    .time(lessThan(500L));
        }

        @DisplayName("Test floot numbers - ")
        @Test
        void test_Float_Numbers() {

            // When
            given().header("Authorization", accessToken)
                    .when()
                    .get(baseUrl + "/api/restassured/odd")
                    .then()
                    .log().ifValidationFails()
                    .assertThat()
                    .body("price", equalTo(1.3f))
                    .body("ck", equalTo(12.2f))
                    .body("name", equalTo("Odd-Data-1"))
                    .time(lessThan(500L));
        }

    }//: End of class RestassuredTest

    private String getAccessToken(String clientId, String secret) {

        return given()
                .auth()
                .preemptive()
                .basic(clientId, secret)
                .with()
                .formParam(this.grantTypeParamName, this.clientCredentialGrantTypeParamValue)
                .when()
                .post(this.tokenRequestUrl)
                .jsonPath()
                .getString(this.jwtTokenNodeName);
    }

    private Map<String, String> getUserParams() {
        return Map.of(
                "username", "tester",
                "email", "tester@tecsys.com",
                "password", "123456",
                "passwordConfirmation", "123456");
    }


}///:~
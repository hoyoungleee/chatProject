package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.common.constants.Constants;
import com.example.demo.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JWTProvider {
    private static String secretKey;
    private static String refreshSecretKey;
    private static long tokenTimeForMinutes;
    private static long refreshTokenTimeForMinutes;

    @Value("${spring.token.secret-key}")
    public static void setSecretKey(String secretKey) {
        JWTProvider.secretKey = secretKey;
    }
    @Value("${spring.token.refresh-secret-key}")
    public static void setRefreshSecretKey(String refreshSecretKey) {
        JWTProvider.refreshSecretKey = refreshSecretKey;
    }

    @Value("${spring.token.token-time}")
    public static void setTokenTimeForMinutes(long tokenTimeForMinutes) {
        JWTProvider.tokenTimeForMinutes = tokenTimeForMinutes;
    }

    @Value("${spring.token.refresh-token-time}")
    public static void setRefreshTokenTimeForMinutes(long refreshTokenTimeForMinutes) {
        JWTProvider.refreshTokenTimeForMinutes = refreshTokenTimeForMinutes;
    }

    public static String createToken(String name) {
       return JWT.create()
               .withSubject(name)
               .withIssuedAt(new Date())
               .withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinutes * Constants.ON_MINUTE_MILLIS))
               .sign(Algorithm.HMAC256(secretKey));
    }

    public static String createRefreshToken(String name) {
        return JWT.create()
                .withSubject(name)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinutes * Constants.ON_MINUTE_MILLIS))
                .sign(Algorithm.HMAC256(secretKey));
    }

    public static DecodedJWT checkAccessTokenForRefresh(String token) {
        try{
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            log.error("token must be Expired: {}", decoded.getSubject());
            throw new CustomException(null, token);
        } catch (AlgorithmMismatchException | SignatureVerificationException | InvalidClaimException e) {
            throw new CustomException(null);

        }catch (TokenExpiredException e){
            return  JWT.decode(token);
        }
    }

    public static DecodedJWT decodeTokenAfterVerify(String token, String key) {

    }

    public static DecodedJWT decodedJWT(String token) {

    }
}

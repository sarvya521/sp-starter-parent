package com.sp.boilerplate.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Configuration
public class CognitoJwtConfig {

    @Autowired
    private CognitoJwtConfigProperties cognitoJwtConfigProperties;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
        ResourceRetriever resourceRetriever =
                new DefaultResourceRetriever(cognitoJwtConfigProperties.getConnectionTimeout(), cognitoJwtConfigProperties.getReadTimeout());
        URL jwkURL= new URL(cognitoJwtConfigProperties.getJwkUrl());
        JWKSource keySource= new RemoteJWKSet(jwkURL, resourceRetriever);
        ConfigurableJWTProcessor jwtProcessor= new DefaultJWTProcessor();
        JWSKeySelector keySelector= new JWSVerificationKeySelector(RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }
}

package com.proxidev.travelapplication.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String accessSecret;
    private String refreshSecret;
    private long accessExpirationMs;
    private long refreshExpirationMs;
}
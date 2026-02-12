package com.proxidev.travelapplication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "tenant")
@Component
@Getter
@Setter
public class TenantProperties {
    private String baseDomain = "travelapp.com";
    private String ignoredSubdomains = "www,api,app,admin";

    public List<String> getIgnoredSubdomainsList() {
        return Arrays.stream(ignoredSubdomains.split(","))
                .map(String::trim)
                .map(String::toLowerCase).toList();
    }
}

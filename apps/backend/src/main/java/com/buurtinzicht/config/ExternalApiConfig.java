package com.buurtinzicht.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Configuration
public class ExternalApiConfig {

    @Value("${belgian-api.statbel.base-url:https://api.statbel.fgov.be/v1}")
    private String statbelBaseUrl;

    @Value("${belgian-api.statbel.api-key:#{null}}")
    private String statbelApiKey;

    @Value("${belgian-api.geo6.base-url:https://api-v2.geo6.be}")
    private String geo6BaseUrl;

    @Value("${belgian-api.geo6.jwt-token:#{null}}")
    private String geo6JwtToken;

    @Value("${belgian-api.bpost.base-url:https://api.bpost.be/services/shm}")
    private String bpostBaseUrl;

    @Value("${belgian-api.bpost.api-key:#{null}}")
    private String bpostApiKey;

    @Value("${belgian-api.timeout.connect:10000}")
    private int connectTimeout;

    @Value("${belgian-api.timeout.read:30000}")
    private int readTimeout;

    @Bean("statbelRestTemplate")
    public RestTemplate statbelRestTemplate(RestTemplateBuilder builder) {
        RestTemplate template = builder
                .rootUri(statbelBaseUrl)
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();

        if (statbelApiKey != null) {
            template.setInterceptors(List.of(
                (ClientHttpRequestInterceptor) (request, body, execution) -> {
                    request.getHeaders().set("X-API-Key", statbelApiKey);
                    return execution.execute(request, body);
                }
            ));
        }

        return template;
    }

    @Bean("geo6RestTemplate")
    public RestTemplate geo6RestTemplate(RestTemplateBuilder builder) {
        RestTemplate template = builder
                .rootUri(geo6BaseUrl)
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();

        if (geo6JwtToken != null) {
            template.setInterceptors(List.of(
                (ClientHttpRequestInterceptor) (request, body, execution) -> {
                    request.getHeaders().set("Authorization", "Bearer " + geo6JwtToken);
                    return execution.execute(request, body);
                }
            ));
        }

        return template;
    }

    @Bean("bpostRestTemplate")
    public RestTemplate bpostRestTemplate(RestTemplateBuilder builder) {
        RestTemplate template = builder
                .rootUri(bpostBaseUrl)
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();

        if (bpostApiKey != null) {
            template.setInterceptors(List.of(
                (ClientHttpRequestInterceptor) (request, body, execution) -> {
                    request.getHeaders().set("Authorization", "Bearer " + bpostApiKey);
                    return execution.execute(request, body);
                }
            ));
        }

        return template;
    }

    @Bean("defaultExternalApiRestTemplate")
    public RestTemplate defaultExternalApiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    public String getStatbelBaseUrl() {
        return statbelBaseUrl;
    }

    public String getGeo6BaseUrl() {
        return geo6BaseUrl;
    }

    public String getBpostBaseUrl() {
        return bpostBaseUrl;
    }
}
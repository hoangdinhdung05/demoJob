package com.demoJob.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Backend backend;
    private Frontend frontend;
    private Admin admin;

    @Data
    public static class Backend {
        private String url;
    }

    @Data
    public static class Frontend {
        private String url;
    }

    @Data
    public static class Admin {
        private String email;
    }
}

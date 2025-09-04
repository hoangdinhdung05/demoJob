package com.demoJob.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.mail.template")
public class MailTemplateProperties {
    private String prefix;
    private String suffix;
    private String encoding;
    private boolean cacheable;
    private long cacheTtlMs;
}

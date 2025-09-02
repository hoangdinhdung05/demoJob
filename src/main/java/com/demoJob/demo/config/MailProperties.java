package com.demoJob.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Properties;

@Component
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol = "smtp";
    private String defaultEncoding = "UTF-8";
    private boolean testConnection = false;

    // Map toàn bộ properties.mail.* vào đây
    private Map<String, String> properties;

    public Properties getJavaMailProperties() {
        Properties props = new Properties();
        if (properties != null) {
            props.putAll(properties); // load thẳng từ YAML
        }
        return props;
    }
}

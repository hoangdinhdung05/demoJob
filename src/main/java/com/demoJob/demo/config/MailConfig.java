package com.demoJob.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import java.util.Properties;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class MailConfig {

    // --- Mail properties ---
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.default-encoding:UTF-8}")
    private String defaultEncoding;

    // Extra mail properties
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    // --- Executor properties ---
    @Value("${app.mail.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${app.mail.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.mail.executor.queue-capacity}")
    private int queueCapacity;

    @Value("${app.mail.executor.thread-name-prefix}")
    private String threadNamePrefix;

    // --- Template properties ---
    @Value("${app.mail.template.prefix}")
    private String templatePrefix;

    @Value("${app.mail.template.suffix}")
    private String templateSuffix;

    @Value("${app.mail.template.encoding}")
    private String templateEncoding;

    @Value("${app.mail.template.cacheable}")
    private boolean cacheable;

    @Value("${app.mail.template.cache-ttl-ms}")
    private Long cacheTtlMs;

    /**
     * Executor phục vụ gửi email bất đồng bộ.
     */
    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    /**
     * Cấu hình JavaMailSender.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding(defaultEncoding);

        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    /**
     * TemplateEngine cho Thymeleaf.
     */
    @Bean(name = "mailTemplateEngine")
    public TemplateEngine mailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(mailTemplateResolver());
        return templateEngine;
    }

    /**
     * TemplateResolver cho Thymeleaf.
     */
    private ITemplateResolver mailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(templatePrefix);
        templateResolver.setSuffix(templateSuffix);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(templateEncoding);
        templateResolver.setCacheable(cacheable);
        templateResolver.setCacheTTLMs(cacheTtlMs);
        return templateResolver;
    }
}

package com.demoJob.demo.config;

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
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class MailConfig {

    private final MailProperties mailProperties;
    private final MailExecutorProperties executorProperties;
    private final MailTemplateProperties templateProperties;

    public MailConfig(MailProperties mailProperties,
                      MailExecutorProperties executorProperties,
                      MailTemplateProperties templateProperties) {
        this.mailProperties = mailProperties;
        this.executorProperties = executorProperties;
        this.templateProperties = templateProperties;
    }

    /**
     * Executor phục vụ gửi email bất đồng bộ.
     * <p>
     * - Dùng ThreadPoolTaskExecutor (Spring-managed).<br>
     * - Cho phép chạy nhiều task gửi mail song song.<br>
     * - Có thread pool, Spring quản lý và tự khởi động lại khi cần.<br>
     *
     * @return Executor dành riêng cho email tasks
     */
    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    /**
     * Cấu hình JavaMailSender dựa trên MailProperties.
     * <p>
     * - Đọc thông tin host, port, username, password từ application.yml.<br>
     * - Dùng để gửi email qua SMTP server.<br>
     *
     * @return JavaMailSender đã cấu hình
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        mailSender.setDefaultEncoding(mailProperties.getDefaultEncoding());
        mailSender.setJavaMailProperties(mailProperties.getJavaMailProperties());
        return mailSender;
    }

    /**
     * Tạo TemplateEngine cho Thymeleaf.
     * <p>
     * - Dùng để render email HTML từ template.<br>
     * - Nếu chưa có bean TemplateEngine nào khác, sẽ tạo mới.<br>
     *
     * @return TemplateEngine cấu hình cho email
     */
    @Bean(name = "mailTemplateEngine")
    public TemplateEngine mailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(mailTemplateResolver());
        return templateEngine;
    }

    /**
     * TemplateResolver cho Thymeleaf.
     * <p>
     * - Đọc template từ classpath: resources/templates/<br>
     * - Định dạng: HTML (.html)<br>
     * - UTF-8 encoding, cache 1 giờ<br>
     *
     * @return ITemplateResolver đọc file template HTML
     */
    private ITemplateResolver mailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(templateProperties.getPrefix());
        templateResolver.setSuffix(templateProperties.getSuffix());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(templateProperties.getEncoding());
        templateResolver.setCacheable(templateProperties.isCacheable());
        templateResolver.setCacheTTLMs(templateProperties.getCacheTtlMs());
        return templateResolver;
    }
}

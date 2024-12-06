package com.example.User.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.password}")
    private String EMAIL_PASSWORD;

    @Value("${spring.mail.username}")
    private String EMAIL_USERNAME;


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(EMAIL_USERNAME);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  //tls 보안 프로토콜 사용 여부
        props.put("mail.debug", "false");  // 개발 시에만 true로 설정
        props.put("mail.smtp.timeout", "5000");

        return mailSender;
    }
}
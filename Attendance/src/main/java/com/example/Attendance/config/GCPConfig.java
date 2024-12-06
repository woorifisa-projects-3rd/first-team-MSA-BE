package com.example.Attendance.config;

import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class GCPConfig {

    @Value("${GCP_JSON}")
    private String credentialsJson;


    @Bean
    public Storage storage() {
        try (ByteArrayInputStream keyFile = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))) {

            return StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(keyFile))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.GCP_SETTING_ERROR);
        }
    }
}
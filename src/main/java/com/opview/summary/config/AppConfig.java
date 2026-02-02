package com.opview.summary.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Gson bean： LocalDateTime 的序列化/反序列化 + 自動處理底線命名
    @Bean
    public Gson gson() {
        GsonBuilder builder = new GsonBuilder();

        // 把 API 的 snake_case 映射到 Java 的 camelCase 
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        // 反序列化 LocalDateTime
        builder.registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    if (json == null || json.getAsString().isEmpty()) return null;
                    String s = json.getAsString();
                    try {
                        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (Exception ex1) {
                        try {
                            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
                        } catch (Exception ex2) {
                            try {
                                return LocalDateTime.parse(s); // fallback ISO 格式
                            } catch (Exception ex3) {
                                return null;
                            }
                        }
                    }
                });

        // 序列化 LocalDateTime
        builder.registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
                    if (src == null) return null;
                    return new com.google.gson.JsonPrimitive(
                            src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    );
                });

        return builder.create();
    }
}

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

    // Gson bean：加入 LocalDateTime 的序列化/反序列化 + 自動處理底線命名
    @Bean
    public Gson gson() {
        GsonBuilder builder = new GsonBuilder();

        // ✅ 自動把 API 的 snake_case (e.g. s_name) 映射到 Java 的 camelCase (sName)
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        // 反序列化 LocalDateTime
        builder.registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    try {
                        if (json == null || json.getAsString().isEmpty()) return null;
                        String s = json.getAsString();
                        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        return LocalDateTime.parse(s, f1);
                    } catch (Exception ex) {
                        try {
                            return LocalDateTime.parse(json.getAsString());
                        } catch (Exception ex2) {
                            return null;
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

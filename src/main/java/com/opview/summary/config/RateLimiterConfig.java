package com.opview.summary.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    /** API 呼叫數限制 (10 RPM, 250 RPD) */
    @Bean
    public Bucket apiCallBucket() {
        Bandwidth rpmLimit = Bandwidth.builder()
                .capacity(10) // 每分鐘最多 10 次
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();

        Bandwidth rpdLimit = Bandwidth.builder()
                .capacity(250) // 每天最多 250 次
                .refillIntervally(250, Duration.ofDays(1))
                .build();

        return Bucket.builder()
                .addLimit(rpmLimit)
                .addLimit(rpdLimit)
                .build();
    }

    /** Token 限制 (250,000 TPM) */
    @Bean
    public Bucket tokenBucket() {
        Bandwidth tpmLimit = Bandwidth.builder()
                .capacity(250_000) // 每分鐘最多 25 萬 token
                .refillIntervally(250_000, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(tpmLimit)
                .build();
    }
}

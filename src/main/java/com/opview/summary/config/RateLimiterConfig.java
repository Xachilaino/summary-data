package com.opview.summary.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    /** API 呼叫數限制 */
    @Bean
    public Bucket apiCallBucket() {
        // [修正] 配合 Gemini 2.5 Flash 免費版限制
        // Google 限制: 5 RPM (每分鐘 5 次)
        // 程式設定: 改為 4 RPM (留一點緩衝空間)
        Bandwidth rpmLimit = Bandwidth.builder()
                .capacity(4) // <--- 從 10 改為 4
                .refillIntervally(4, Duration.ofMinutes(1))
                .build();

        // [注意] 根據你的 Log，每日限制似乎是 20 次？(RPD: 6/20)
        // 建議先保守設定，如果確認是 1500 再改回來
        Bandwidth rpdLimit = Bandwidth.builder()
                .capacity(20) // <--- 從 250 改為 20 (根據 Log 觀察)
                .refillIntervally(20, Duration.ofDays(1))
                .build();

        return Bucket.builder()
                .addLimit(rpmLimit)
                .addLimit(rpdLimit)
                .build();
    }

    /** Token 限制 (維持不變或根據需求調整) */
    @Bean
    public Bucket tokenBucket() {
        Bandwidth tpmLimit = Bandwidth.builder()
                .capacity(250_000) 
                .refillIntervally(250_000, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(tpmLimit)
                .build();
    }
}
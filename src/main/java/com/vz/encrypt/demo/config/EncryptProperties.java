package com.vz.encrypt.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author visy.wang
 * @description: 加密相关配置
 * @date 2023/7/25 10:14
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.encrypt")
public class EncryptProperties {
    private String key;
}

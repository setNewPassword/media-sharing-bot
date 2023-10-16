package com.btard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.btard.utils.CryptoTool;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfig {
    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool getCryptoTool() {
        return new CryptoTool(salt);
    }
}

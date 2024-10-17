package com.damlotec.ecommerce.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.mongodb")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class ConfigParams {
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

}

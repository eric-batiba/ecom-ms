package com.damlotec.ecommerce.customer;

import com.damlotec.ecommerce.config.ConfigParams;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
@RequiredArgsConstructor
@RefreshScope
public class ConfigController {
    private final ConfigParams configParams;

    @GetMapping("/global")
    public ConfigParams getConfig() {
        return configParams;
    }
}

package org.darktech.controller;

import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthCheckController {

    private final HealthEndpoint healthEndpoint;

    public HealthCheckController(HealthEndpoint healthEndpoint){
        this.healthEndpoint=healthEndpoint;
    }
    @GetMapping("/health")
    public HealthComponent healthCheck(){
        return healthEndpoint.health();
    }
}

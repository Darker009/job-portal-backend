package org.darktech.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthCheck implements HealthIndicator {

    @Override
    public Health health(){
        boolean isHealthy = checkCustomComponent();
        if (isHealthy){
            return Health.up().withDetail("Service", "Available").build();
        }else {
            return Health.down().withDetail("Service", "Unavailable").build();
        }
    }

    private boolean checkCustomComponent(){
        return true;
    }
}

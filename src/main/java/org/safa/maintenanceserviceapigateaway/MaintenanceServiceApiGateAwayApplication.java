package org.safa.maintenanceserviceapigateaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MaintenanceServiceApiGateAwayApplication {

    static void main(String[] args) {
        SpringApplication.run(MaintenanceServiceApiGateAwayApplication.class, args);
    }
}

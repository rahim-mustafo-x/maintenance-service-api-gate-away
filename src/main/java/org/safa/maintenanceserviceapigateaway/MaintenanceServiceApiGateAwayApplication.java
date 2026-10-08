package org.safa.maintenanceserviceapigateaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class MaintenanceServiceApiGateAwayApplication {
    @GetMapping("/")
    public ResponseEntity<Resource> index(){
        Resource indexHtml = new ClassPathResource("static/index.html");
        return ResponseEntity.ok(indexHtml);
    }

    static void main(String[] args) {
        SpringApplication.run(MaintenanceServiceApiGateAwayApplication.class, args);
    }
}

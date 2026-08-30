package org.safa.maintenanceserviceapigateaway;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "PORT=8090",
        "EUREKA_USERNAME=rahim.mustafo.x",
        "EUREKA_PASSWORD=mustafo18122009",
        "EUREKA_URL=192.168.1.200:8761/eureka",
        "JWT_SECRET_KEY=eOh2IR+JLQ618MBYioH1Dp349jFIPncGPjLOJEZkLAw="
})
class MaintenanceServiceApiGateAwayApplicationTests {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setApi() {
        RestAssured.baseURI="http://localhost";
        RestAssured.port=port;
    }

    @Test
    void contextLoads() {
        RestAssured.given()
                .contentType("application/json")
                .get("/v3/api-docs")
                .then()
                .log()
                .all();

    }

}

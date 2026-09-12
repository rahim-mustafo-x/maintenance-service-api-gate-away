package org.safa.maintenanceserviceapigateaway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .route(path("/user-service/**"), http())
                .filter(lb("USER-SERVICE"))
                .filter(stripPrefix(1))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> imageServiceRoute() {
        return route("image-service")
                .route(path("/image-service/**"), http())
                .filter(lb("IMAGE-SERVICE"))
                .filter(stripPrefix(1))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> jobServiceRoute() {
        return route("job-service")
                .route(path("/job-service/**"), http())
                .filter(lb("JOB-SERVICE"))
                .filter(stripPrefix(1))
                .build();
    }
}
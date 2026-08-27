package org.safa.maintenanceserviceapigateaway.security;

import lombok.RequiredArgsConstructor;
import org.safa.maintenanceserviceapigateaway.jwt.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //this is jwt token needs for before basic security to make it basic forum
    private final JwtFilter filter;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/favicon.ico",
            "/v3/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/{service}/swagger-ui/**",
            "/{service}/api-docs/**",
            "/{service}/v3/api-docs/**",
            "/{service}/auth/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request->
                                        request.requestMatchers(
                                                PUBLIC_ENDPOINTS
                                        ).permitAll()
                                        .requestMatchers(HttpMethod.GET, "/{service}/v1/image/**").permitAll()
                                        .requestMatchers(HttpMethod.PATCH, "/{service}/v1/image/**").authenticated()
                                        .requestMatchers(HttpMethod.POST, "/{service}/v1/image/**").authenticated()
                                        .requestMatchers(HttpMethod.PUT, "/{service}/v1/image/**").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/{service}/v1/image/**").authenticated()
                                        .requestMatchers("/actuator/**").authenticated()
                                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
    //this is for security of basic forum
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }
}
package com.escola.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/auth/**",
            "/admin/**",
            "/clients/**",
            "/utils/**",
            "/graphql",
            "/graphiql",
            "/",
            "/error"
    );

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // Habilita a configuração de CORS definida no bean 'corsConfigurationSource'
                .cors(Customizer.withDefaults())
                // Desabilita CSRF, uma prática padrão e segura para gateways de API stateless
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Define as regras de autorização de acesso
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Bean que define a configuração central de CORS para o Gateway.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // SUGESTÃO: Para produção, é uma boa prática tornar esta lista configurável
        // através do application.yml, em vez de deixá-la fixa no código.
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
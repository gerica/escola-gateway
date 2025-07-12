package com.escola.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuração de segurança para a aplicação reativa (Spring Cloud Gateway).
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // 1. Desabilita explicitamente os mecanismos de login padrão
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 2. Define as regras de autorização
                .authorizeExchange(exchange -> exchange
                        // Permite acesso PÚBLICO e irrestrito a estes caminhos.
                        .pathMatchers("/", "/graphiql", "/graphql").permitAll()

                        // Para TODAS as outras requisições, exige autenticação.
                        .anyExchange().authenticated()
                );

        return http.build();
    }
}
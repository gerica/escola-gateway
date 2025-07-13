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

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // Habilita a configuração de CORS definida no bean 'corsConfigurationSource'
                .cors(Customizer.withDefaults())
                // Desabilita CSRF, uma prática padrão e segura para gateways de API stateless
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Define as regras de autorização de acesso
                .authorizeExchange(exchange -> exchange
                        // CORREÇÃO: Permite acesso público a TODAS as rotas do serviço de autenticação.
                        // O 'auth-service' é responsável por sua própria segurança interna.
                        // Isso libera caminhos como /auth/graphql e /auth/graphiql.
                        .pathMatchers("/auth/**").permitAll()

                        // SUGESTÃO: Libere também endpoints de documentação da API, se você os usar.
                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // Exige autenticação para qualquer outra requisição que não foi liberada acima.
                        // É por causa desta linha que o formLogin aparece se você tentar
                        // acessar, por exemplo, /clients/** sem estar logado.
                        .anyExchange().authenticated()
                )
                // Habilita um formulário de login básico.
                // Em um sistema real, isso será substituído por um filtro de validação de token JWT.
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
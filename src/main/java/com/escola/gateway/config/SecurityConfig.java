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

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                // Mantemos a configuração de CORS e CSRF, pois são boas práticas e raramente causam este problema.
//                .cors(Customizer.withDefaults())
//                .csrf(csrf -> csrf.disable())
//                // --- DEBUG: DESABILITANDO TODA A SEGURANÇA ---
//                // A linha abaixo permite TODAS as requisições, sem exceção.
//                .authorizeExchange(exchange -> exchange.anyExchange().permitAll());
//
//        // O formLogin não é mais necessário, pois não há nada para se autenticar.
//        // .formLogin(Customizer.withDefaults());
//
//        return http.build();
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // Habilita a configuração de CORS definida no bean abaixo
                .cors(Customizer.withDefaults())
                // Desabilita CSRF, uma prática comum para gateways de API
                .csrf(csrf -> csrf.disable())
                // Define as regras de autorização de acesso
                .authorizeExchange(exchange -> exchange
                        // CORREÇÃO: Permite acesso público ao GraphiQL e a TODOS os seus assets (css, js, etc.)
                        .pathMatchers("/graphiql/**").permitAll()
                        // Permite acesso ao endpoint principal do GraphQL. A segurança a nível de campo será feita pelo GraphQL.
                        .pathMatchers("/graphql/**").permitAll()
                        // Exige autenticação para qualquer outra requisição que não foi liberada acima
                        .anyExchange().authenticated()
                )
                // Habilita um formulário de login básico para facilitar testes
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Bean que define a configuração central de CORS para o Gateway.
     * Usamos as classes do pacote 'org.springframework.web.cors.reactive'.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite requisições da sua aplicação frontend (ex: Angular rodando na porta 4200)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Define os métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("POST", "GET", "OPTIONS"));
        // Permite todos os cabeçalhos
        configuration.setAllowedHeaders(List.of("*"));
        // Permite o envio de credenciais (cookies, tokens de autorização)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todos os caminhos do Gateway
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
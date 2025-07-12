package com.escola.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GraphQLFederationFilter extends AbstractGatewayFilterFactory<GraphQLFederationFilter.Config> {

    private final WebClient webClient;

    public GraphQLFederationFilter(@LoadBalanced WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        String query = new String(bytes, StandardCharsets.UTF_8);

                        // Use o nome do serviço registrado no Eureka (em minúsculas)
                        return webClient.post()
                                .uri("http://auth-service/internal/auth/graphql")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header("X-Internal-Request", "true")
                                .bodyValue(query)
                                .retrieve()
                                .bodyToMono(String.class)
                                .flatMap(response -> {
                                    return exchange.getResponse()
                                            .writeWith(Mono.just(
                                                    exchange.getResponse().bufferFactory().wrap(response.getBytes())
                                            ));
                                });
                    });
        };
    }

    public static class Config {
        // Configurações adicionais se necessário
    }
}
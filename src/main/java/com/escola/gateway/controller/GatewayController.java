package com.escola.gateway.controller;


import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class GatewayController {

    private final RouteDefinitionLocator routeDefinitionLocator;

    // Injetamos o localizador de rotas para podermos listar as rotas ativas
    public GatewayController(RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @QueryMapping
    public Mono<GatewayStatus> gatewayStatus() {
        // Coleta os IDs de todas as rotas configuradas (auth-service-route, etc.)
        Mono<List<String>> activeRoutesMono = this.routeDefinitionLocator.getRouteDefinitions()
                .map(routeDefinition -> routeDefinition.getId())
                .collectList();

        // Retorna um objeto que corresponde ao tipo GatewayStatus do schema
        return activeRoutesMono.map(routes -> new GatewayStatus("ONLINE", routes));
    }

    // DTO para representar a resposta
    // Pode ser uma classe record, uma classe normal ou uma classe interna.
    public record GatewayStatus(String status, List<String> activeRoutes) {
    }
}
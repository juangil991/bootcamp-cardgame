package org.example.cardgame.application.command.adapter.service;

import org.example.cardgame.usecase.gateway.ListaDeCartaService;
import org.example.cardgame.usecase.gateway.model.CartaMaestra;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class DataConsultarCartaMaestraService implements ListaDeCartaService {

    public DataConsultarCartaMaestraService() {

    }


    @Override
    public Flux<CartaMaestra> obtenerCartasPepsico() {
        return Flux.just();
    }
}

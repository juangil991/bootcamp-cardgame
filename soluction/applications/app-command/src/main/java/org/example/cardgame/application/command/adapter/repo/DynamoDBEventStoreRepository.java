package org.example.cardgame.application.command.adapter.repo;

import org.example.cardgame.generic.EventStoreRepository;
import org.example.cardgame.generic.StoredEvent;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Component
public class DynamoDBEventStoreRepository implements EventStoreRepository {

    private final DynamoDBRepository template;
    private final EventSerializer eventSerializer;


    public DynamoDBEventStoreRepository(DynamoDBRepository template, EventSerializer eventSerializer) {
        this.template = template;
        this.eventSerializer = eventSerializer;
    }

    @Override
    public Flux<DomainEvent> getEventsBy(String aggregateName, String aggregateRootId) {
        return Flux.fromIterable(template.findByAggregateRootId(aggregateRootId))
                .sort(Comparator.comparing(event -> event.getStoredEvent().getOccurredOn()))
                .map(storeEvent -> storeEvent.getStoredEvent().deserializeEvent(eventSerializer));
    }

    @Override
    public Mono<Void> saveEvent(String aggregateName, String aggregateRootId, StoredEvent storedEvent) {
        var eventStored = new DocumentEventStored();
        eventStored.setAggregateRootId(aggregateRootId);
        eventStored.setStoredEvent(storedEvent);
        return Mono.just(template.save(eventStored)).then();
    }
}


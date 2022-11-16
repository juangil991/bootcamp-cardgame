package org.example.cardgame.application.command.adapter.repo;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@EnableScan
public interface DynamoDBRepository extends CrudRepository<DocumentEventStored, String> {
    Iterable<DocumentEventStored> findByAggregateRootId(String aggregateRootId);
}
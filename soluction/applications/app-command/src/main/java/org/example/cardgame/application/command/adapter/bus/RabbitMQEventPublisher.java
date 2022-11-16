package org.example.cardgame.application.command.adapter.bus;

import brave.Span;
import brave.Tracer;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import org.example.cardgame.application.command.ConfigProperties;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.generic.EventPublisher;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQEventPublisher implements EventPublisher {
    private final Tracer tracer;
    private final EventSerializer eventSerializer;
    private final ConfigProperties configProperties;
    private final AmazonSNS amazonSNS;

    public RabbitMQEventPublisher(Tracer tracer, EventSerializer eventSerializer, ConfigProperties configProperties, AmazonSNS amazonSNS) {
        this.tracer = tracer;
        this.eventSerializer = eventSerializer;
        this.configProperties = configProperties;
        this.amazonSNS = amazonSNS;
    }

    @Override
    public void publish(DomainEvent event) {
        var eventBody = eventSerializer.serialize(event);

        Span span = createSpan(event, eventBody);

        var notification = new Notification(
                event.getClass().getTypeName(),
                eventBody,
                span.context().traceId(),
                span.context().parentId(),
                span.context().spanId(),
                span.context().extra()
        );

        var request = new PublishRequest();
        //TODO: create request
        request.setMessage(notification.serialize());
        var result = amazonSNS.publish(request);
    }

    private Span createSpan(DomainEvent event, String eventBody) {
        return tracer.nextSpan()
                .name("publisher")
                .tag("eventType", event.type)
                .tag("aggregateRootId", event.aggregateRootId())
                .tag("aggregate", event.getAggregateName())
                .tag("uuid", event.uuid.toString())
                .annotate(eventBody)
                .start();
    }

    @Override
    public void publishError(Throwable errorEvent) {

    }





}
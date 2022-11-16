package org.example.cardgame.application.command.adapter.bus;


import com.amazonaws.services.sqs.AmazonSQS;

import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.example.cardgame.application.command.ConfigProperties;
import org.example.cardgame.application.command.handle.BusinessLookUp;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class RabbitMQEventConsumer  {
    private final ConfigProperties configProperties;
    private final EventSerializer eventSerializer;
    private final BusinessLookUp businessLookUp;
    private final AmazonSQS amazonSQS;

    public RabbitMQEventConsumer(ConfigProperties configProperties, EventSerializer eventSerializer, BusinessLookUp businessLookUp, AmazonSQS amazonSQS){
        this.configProperties = configProperties;
        this.eventSerializer = eventSerializer;
        this.businessLookUp = businessLookUp;
        this.amazonSQS = amazonSQS;
    }


    @Scheduled(fixedDelay = 1000)
    public void receive() {
        var receiveMessageResult = amazonSQS.receiveMessage(
                new ReceiveMessageRequest()
                        .withWaitTimeSeconds(5)
                       // .withQueueUrl(queueUrl)
        );

        receiveMessageResult.getMessages().forEach(message -> {
            var notification = Notification.from(message.getBody());
            try {
                DomainEvent event = eventSerializer.deserialize(
                        notification.getBody(), Class.forName(notification.getType())
                );
                 businessLookUp.get(event.type)
                        .flatMap(service -> service.doProcessing(event))
                        .subscribe();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}

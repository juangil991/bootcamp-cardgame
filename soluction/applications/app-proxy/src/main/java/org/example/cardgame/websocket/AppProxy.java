package org.example.cardgame.websocket;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EnableWebSocket
public class AppProxy {
    public static void main(String[] args) {
        SpringApplication.run(AppProxy.class, args);
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConfigProperties configProperties){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(URI.create(configProperties.getUriBus()));
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ConnectionFactory connectionFactory(ConfigProperties configProperties) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setUri(configProperties.getUriBus());
        return connectionFactory;
    }
}

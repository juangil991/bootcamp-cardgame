package org.example.cardgame.application.command;



import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import org.example.cardgame.generic.EventPublisher;
import org.example.cardgame.generic.EventStoreRepository;
import org.example.cardgame.generic.IntegrationHandle;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import javax.annotation.PostConstruct;
import java.util.Arrays;



@Configuration
@EnableScheduling
@ComponentScan(value="org.example.cardgame.usecase",
        useDefaultFilters = false, includeFilters = @ComponentScan.Filter
        (type = FilterType.REGEX, pattern = ".*UseCase")
)
public class ApplicationConfig {


    private final AmazonSQS amazonSQS;
    private final AmazonSNS amazonSNS;
    private final ConfigProperties configProperties;

    public ApplicationConfig(AmazonSQS amazonSQS, AmazonSNS amazonSNS, ConfigProperties configProperties) {
        this.amazonSQS = amazonSQS;
        this.amazonSNS = amazonSNS;
        this.configProperties = configProperties;
    }

    @PostConstruct
    public void init() {
        var topicArn = amazonSNS.createTopic(configProperties.getExchange()).getTopicArn();
        var queueUrl = amazonSQS.createQueue(configProperties.getQueue()).getQueueUrl();
        Topics.subscribeQueue(amazonSNS, amazonSQS, topicArn, queueUrl);
    }




    @Bean
    public IntegrationHandle integrationHandle(EventStoreRepository repository, EventPublisher eventPublisher, EventSerializer eventSerializer){
        return new IntegrationHandle(configProperties.getStoreName(), repository, eventPublisher, eventSerializer);
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }


}


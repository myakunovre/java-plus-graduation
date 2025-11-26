package ru.practicum.stats.collector.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ToString
@ConfigurationProperties("collector.kafka")
public class KafkaConfig {
    private ProducerConfig producer;

    @Getter
    @Setter
    public static class ProducerConfig {
        private final Properties properties;
        private final Map<String, String> topics;

        public ProducerConfig(Properties properties, Map<String, String> topics) {
            this.properties = properties;
            this.topics = topics;
        }
    }
}
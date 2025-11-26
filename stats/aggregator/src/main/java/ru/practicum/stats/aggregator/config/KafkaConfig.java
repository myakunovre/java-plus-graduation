package ru.practicum.stats.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ToString
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private Producer producer;
    private Consumer consumer;
    private Map<String, String> topics;

    @Getter
    @Setter
    public static class Consumer {
        private Properties properties;
    }

    @Getter
    @Setter
    public static class Producer {
        private Properties properties;
    }

    public enum TopicType {
        USER_ACTIONS("user-actions"),
        EVENTS_SIMILARITY("events-similarity");

        private final String topicName;

        TopicType(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicName() {
            return topicName;
        }
    }

    public String getTopic(TopicType type) {
        String topic = topics.get(type.getTopicName());
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Неизвестный тип топика: " + type);
        }
        return topic;
    }
}
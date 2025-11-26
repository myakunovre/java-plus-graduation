package ru.practicum.stats.analyzer.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("analyzer.kafka")
public class KafkaConfig {

    private ConsumerProperties userActionsConsumer;
    private ConsumerProperties eventSimilarityConsumer;
    private Map<String, String> topics;

    @Getter
    @Setter
    public static class ConsumerProperties {
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
        return Optional.ofNullable(topics.get(type.getTopicName()))
                .filter(topic -> !topic.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип топика: " + type));
    }
}
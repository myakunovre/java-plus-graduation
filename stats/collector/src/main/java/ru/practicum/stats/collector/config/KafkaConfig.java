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
        //        private Map<String, String> properties;
        //        private final EnumMap<TopicType, String> topics = new EnumMap<>(TopicType.class);
        private final Map<String, String> topics;

        public ProducerConfig(Properties properties, Map<String, String> topics) {
            this.properties = properties;
            this.topics = topics;
        }

//        public ProducerConfig(Properties properties, Map<String, String> topics) {
//            this.properties = properties;
//            for (Map.Entry<String, String> entry : topics.entrySet()) {
//                this.topics.put(TopicType.from(entry.getKey()), entry.getValue());
//            }
//        }
    }

//    public enum TopicType {
//        USER_ACTIONS;
//
//        public static TopicType from(String type) {
//            // Нормализуем строку для сравнения
//            String normalizedType = type.replace("-", "_").toUpperCase();
//            for (TopicType value : values()) {
//                if (value.name().equals(normalizedType)) {
//                    return value;
//                }
//            }
//            return null;
//        }
//    }
}
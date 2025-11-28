package ru.practicum.stats.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.config.KafkaConfig;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
public class AggregationStarter {

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private final EnumMap<KafkaConfig.TopicType, String> topics = new EnumMap<>(KafkaConfig.TopicType.class);

    private final KafkaConsumer<Long, SpecificRecordBase> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final EventSimilarityServiceImpl eventSimilarityService;

    private final Duration consumeAttemptTimeout;



    public AggregationStarter(KafkaConfig kafkaConfig,
                              EventSimilarityServiceImpl eventSimilarityService) {
        this.consumer = new KafkaConsumer<>(kafkaConfig.getConsumer().getProperties());
        this.producer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
        this.eventSimilarityService = eventSimilarityService;
        this.consumeAttemptTimeout = Duration.ofMillis(kafkaConfig.getConsumeAttemptTimeoutMillis());
        for (KafkaConfig.TopicType type : KafkaConfig.TopicType.values()) {
            topics.put(type, kafkaConfig.getTopic(type));
        }
    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения действий пользователей по мероприятиям,
     * пересчитывает похожесть мероприятий и записывает в кафку.
     */
    public void start() {
        try {
            consumer.subscribe(List.of(topics.get(KafkaConfig.TopicType.USER_ACTIONS)));

            while (true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(consumeAttemptTimeout);

                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    UserActionAvro userAction = handleRecord(record);
                    Optional<List<EventSimilarityAvro>> eventSimilarities = eventSimilarityService.updateState(userAction);
                    eventSimilarities.ifPresent(this::sendEventSimilarities);

                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }

                if (!currentOffsets.isEmpty()) {
                    consumer.commitAsync();
                }
            }

        } catch (WakeupException ignored) {
            log.info("Consumer shutdown detected.");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                producer.flush();
                if (!currentOffsets.isEmpty()) {
                    consumer.commitSync(currentOffsets);
                }

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private UserActionAvro handleRecord(ConsumerRecord<Long, SpecificRecordBase> record) {
        log.info("Received record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof UserActionAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (UserActionAvro) record.value();
    }

    private void sendEventSimilarities(List<EventSimilarityAvro> eventSimilarities) {
        String topic = topics.get(KafkaConfig.TopicType.EVENTS_SIMILARITY);

        for (EventSimilarityAvro eventSimilarity : eventSimilarities) {
            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, eventSimilarity);
            log.info("Отправляем record: {} \n", record);
            producer.send(record);
        }

        producer.flush();
        log.info("Отправили весь record \n");
    }
}

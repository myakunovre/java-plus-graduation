package ru.practicum.stats.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.config.KafkaConfig;
import ru.practicum.stats.analyzer.dal.service.eventSimilarity.EventSimilarityService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class EventSimilarityProcessor implements Runnable, DisposableBean {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final EventSimilarityService eventSimilarityService;
    private final String topic;
    private final AtomicBoolean running = new AtomicBoolean(true); // Флаг для контроля цикла

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public EventSimilarityProcessor(KafkaConfig config, EventSimilarityService eventSimilarityService) {
        this.consumer = new KafkaConsumer<>(config.getEventSimilarityConsumer().getProperties());
        this.eventSimilarityService = eventSimilarityService;
        this.topic = config.getTopic(KafkaConfig.TopicType.EVENTS_SIMILARITY);
    }

    public void run() {
        log.info("EventSimilarityProcessor started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Waking up snapshotConsumer...");
            consumer.wakeup();
        }));
        try {
            consumer.subscribe(List.of(topic));
            while (running.get()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    EventSimilarityAvro eventSimilarityAvro = handleRecord(record);
                    eventSimilarityService.saveEventSimilarity(eventSimilarityAvro);
                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }

                if (!currentOffsets.isEmpty()) {
                    consumer.commitAsync(new HashMap<>(currentOffsets), (offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Failed to commit offsets in eventSimilarityConsumer: {}", offsets, exception);
                        }
                    });
                }
            }
        } catch (WakeupException e) {
            log.info("eventSimilarityConsumer shutdown detected.");
        } catch (Exception e) {
            log.error("Error in EventSimilarityProcessor", e);
        } finally {
            try {
                if (!currentOffsets.isEmpty()) {
                    consumer.commitSync(currentOffsets);
                }
            } finally {
                log.info("Closing eventSimilarityConsumer");
                consumer.close();
            }
        }
    }

    private EventSimilarityAvro handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("Received event-similarity record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof EventSimilarityAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (EventSimilarityAvro) record.value();
    }

    @Override
    public void destroy() {
        log.info("EventSimilarityProcessor: Destroy method called. Attempting to stop consumer.");
        running.set(false);
        consumer.wakeup();
    }
}
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
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.config.KafkaConfig;
import ru.practicum.stats.analyzer.dal.service.userAction.UserActionService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class UserActionProcessor implements Runnable, DisposableBean {
    private final KafkaConsumer<Long, SpecificRecordBase> consumer;
    private final UserActionService userActionService;
    private final String topic;
    private final AtomicBoolean running = new AtomicBoolean(true); // Флаг для контроля цикла

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

//    public UserActionProcessor(KafkaConfig config, HubEventService hubEventService) {
    public UserActionProcessor(KafkaConfig config, UserActionService userActionService) {
        this.consumer = new KafkaConsumer<>(config.getUserActionsConsumer().getProperties());
        this.userActionService = userActionService;
        this.topic = config.getTopic(KafkaConfig.TopicType.USER_ACTIONS);
    }

    @Override
    public void run() {
        log.info("UserActionProcessor started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Waking up hubConsumer...");
            consumer.wakeup();
        }));
        try {
            consumer.subscribe(List.of(topic));
            while (running.get()) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    UserActionAvro userActionAvro = handleRecord(record);
                    userActionService.saveUserAction(userActionAvro);
                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }

                if (!currentOffsets.isEmpty()) {
                    consumer.commitAsync(new HashMap<>(currentOffsets), (offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Failed to commit offsets in snapshotConsumer: {}", offsets, exception);
                        }
                    });
                }
            }
        } catch (WakeupException e) {
            log.info("userActionConsumer shutdown detected.");
        } catch (Exception e) {
            log.error("Unexpected error in UserActionProcessor", e);
        } finally {
            try {
                if (!currentOffsets.isEmpty()) {
                    consumer.commitSync(currentOffsets);
                }
            } finally {
                log.info("Closing snapshotConsumer");
                consumer.close();
            }
        }

    }

    private UserActionAvro handleRecord(ConsumerRecord<Long, SpecificRecordBase> record) {
        log.info("Received user-action record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof UserActionAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (UserActionAvro) record.value();
    }

    @Override
    public void destroy() {
        log.info("UserActionProcessor: Destroy method called. Attempting to stop consumer.");
        running.set(false);
        consumer.wakeup();
    }
}
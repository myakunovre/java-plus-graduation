package ru.practicum.stats.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.collector.config.KafkaConfig;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class KafkaUserActionProducer implements AutoCloseable {

    protected final KafkaProducer<Long, SpecificRecordBase> producer;
//    protected final KafkaProducer<String, SpecificRecordBase> producer;
//    protected final KafkaProducer<String, UserActionAvro> producer;
    //    protected final EnumMap<TopicType, String> topics;
    private final String topic;

    public KafkaUserActionProducer(KafkaConfig kafkaConfig) {
//        this.topics = kafkaConfig.getProducer().getTopics();
        this.topic = kafkaConfig.getProducer().getTopics().get("user-actions");
        this.producer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
    }

//    public void send(SpecificRecordBase userAction, String hubId, Instant timeStamp, KafkaConfig.TopicType topicType) {
//    public void send(SpecificRecordBase userAction,Instant timeStamp, KafkaConfig.TopicType topicType) {
    public void send(SpecificRecordBase userAction, Instant timeStamp) {
//    public void send(SpecificRecordBase userAction, String userId, Instant timeStamp) {
//    public void send(SpecificRecordBase userAction) {
//    public void send(UserActionAvro userAction) {

        Long key = ((UserActionAvro) userAction).getUserId();
//        String key = String.valueOf(((UserActionAvro) userAction).getUserId());
//        String topic = topics.get(topicType);
        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(
//        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
//        ProducerRecord<String, UserActionAvro> record = new ProducerRecord<>(
                topic,
                null,
                timeStamp.toEpochMilli(),
//                hubId,
//                null,
//                key,
//                userId,
//                String.valueOf(userAction.getUserId()),
                key,
                userAction
        );

        log.trace("Сохраняю действие пользователя {} в топик {}",
                key, topic);

        log.info("<== Json: {}", userAction);
        producer.send(record);
    }

    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}

//package ru.practicum.stats.collector.service.handler.userAction;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import ru.practicum.ewm.stats.avro.ActionTypeAvro;
//import ru.practicum.ewm.stats.avro.UserActionAvro;
//import ru.practicum.ewm.stats.proto.ActionTypeProto;
//import ru.practicum.ewm.stats.proto.UserActionProto;
//import ru.practicum.stats.collector.config.KafkaConfig;
//import ru.practicum.stats.collector.service.KafkaUserActionProducer;
//
//import java.time.Instant;
//
////import static ru.yandex.practicum.telemetry.collector.config.KafkaConfig.TopicType.SENSORS_EVENTS;
//
//@Slf4j
//@RequiredArgsConstructor
//public class UserActionHandler {
//
//    protected final KafkaUserActionProducer producer;
//
//    //    protected UserActionAvro mapToAvro(UserActionProto userActionProto);
//    public ActionTypeProto getMessageType() {
//        return KafkaConfig.TopicType.USER_ACTIONS;
//    }
//
//    public void handle(UserActionProto userAction) {
////        if (!userAction.getActionType().equals(getMessageType())) {
////            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
////        }
//
////        T payload = mapToAvro(event);
//
//        com.google.protobuf.Timestamp protoTimestamp = userAction.getTimestamp();
//        Instant instant = Instant.ofEpochSecond(
//                protoTimestamp.getSeconds(),
//                protoTimestamp.getNanos()
//        );
//
//        ActionTypeAvro actionTypeAvro = ActionTypeAvro.valueOf(userAction.getActionType().name());
//
//        UserActionAvro userActionAvro = UserActionAvro.newBuilder()
//                .setUserId(userAction.getUserId())
//                .setEventId(userAction.getEventId())
//                .setTimestamp(instant)
//                .setActionType(actionTypeAvro)
//                .build();
//
////        producer.send(userActionAvro, event.getHubId(), instant, SENSORS_EVENTS);
//        producer.send(userActionAvro, instant, KafkaConfig.TopicType.USER_ACTIONS);
//    }
//}
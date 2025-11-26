package ru.practicum.ewm.client.stats;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Slf4j
@Service
public class CollectorClient {
    // есть код в вебинаре 9:33
    // куда-то добавить grpc Collector

    private final UserActionControllerGrpc.UserActionControllerBlockingStub actionClient;

    public CollectorClient(@GrpcClient("collector") UserActionControllerGrpc.UserActionControllerBlockingStub actionClient) {
        this.actionClient = actionClient;
    }

    public void saveView(long userId, long eventId) {
        saveUserInteraction(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    public void saveRegister(long userId, long eventId) {
        saveUserInteraction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }

    public void saveLike(long userId, long eventId) {
        saveUserInteraction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    private void saveUserInteraction(long userId, long eventId, ActionTypeProto actionType) {
        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();

        actionClient.collectUserAction(userActionProto);
    }
}

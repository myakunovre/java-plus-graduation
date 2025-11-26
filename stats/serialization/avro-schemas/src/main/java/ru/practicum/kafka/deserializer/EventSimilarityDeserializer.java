package ru.practicum.kafka.deserializer;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public class EventSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public EventSimilarityDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}

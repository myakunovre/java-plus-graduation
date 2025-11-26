package ru.practicum.stats.analyzer.dal.service.eventSimilarity;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityService {
    void saveEventSimilarity(EventSimilarityAvro eventSimilarityAvro);
}

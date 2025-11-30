package ru.practicum.stats.analyzer.dal.service.eventSimilarity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;
import ru.practicum.stats.analyzer.dal.repository.EventSimilarityRepository;
import ru.practicum.stats.analyzer.mapper.EventSimilarityMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository repository;
    private final EventSimilarityMapper mapper;

    @Override
    public void saveEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        log.info("Сохранение в бд событие сходства: {}", eventSimilarityAvro);
        EventSimilarity event = mapper.toEventSimilarity(eventSimilarityAvro);
        EventSimilarity savedEvent = repository.save(event);
        log.info("Событие сходства сохранено: {}", savedEvent);
    }
}
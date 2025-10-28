package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsFeinClient;
import ru.practicum.dto.output.GetStatisticDto;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class StatClientService {
//    private final StatsClient statsClient;
    private final StatsFeinClient statsClient;

    public Map<Long, Long> getEventsView(List<Event> events) {
        //eventId, views
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .toList();

        if (publishedEvents.isEmpty()) {
            return views;
        }

        Optional<LocalDateTime> minPublishedOn = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minPublishedOn.isEmpty()) {
            return views;
        }

        LocalDateTime start = minPublishedOn.get();


        List<String> uri = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .toList();

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        List<GetStatisticDto> stats = statsClient.getStats(start, LocalDateTime.now(), uri, true);
        List<GetStatisticDto> stats = statsClient.getStatistic(
                start.format(dateTimeFormat),
                LocalDateTime.now().format(dateTimeFormat),
                uri,
                true
        );

        stats.forEach(statDto -> {
            String[] parts = statDto.getUri().split("/");
            if (parts.length >= 3) {
                Long eventId = Long.parseLong(parts[2]);
                views.put(eventId, statDto.getHits());
            }
        });
        return views;
    }
}

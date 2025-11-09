package ru.practicum.client;

import org.springframework.stereotype.Component;
import ru.practicum.dto.output.GetStatisticDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class StatsClientFallbackFactory {

    public List<GetStatisticDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        return new ArrayList<>();
    }
}
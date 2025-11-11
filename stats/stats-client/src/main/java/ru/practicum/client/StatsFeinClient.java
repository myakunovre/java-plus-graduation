package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;

import java.util.List;

@FeignClient(name = "stats-server", fallbackFactory =  StatsClientFallbackFactory.class)
public interface StatsFeinClient {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void addHit(@RequestBody StatisticDto statisticDto);

    @GetMapping("/stats")
    List<GetStatisticDto> getStatistic(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false, defaultValue = "") List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique
    );
}
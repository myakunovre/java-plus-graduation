package interaction.client;

import interaction.model.request.ParticipationRequestDtoOut;
import interaction.model.request.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "request-service", fallbackFactory = RequestClientFallbackFactory.class)
public interface RequestFeignClient {

    @GetMapping("/requests/{id}")
    ParticipationRequestDtoOut getById(@PathVariable("id") Long id);

    @GetMapping("/requests/by-ids")
    List<ParticipationRequestDtoOut> getByIds(@RequestParam List<Long> requestIds);

    @GetMapping("/requests/by-event-id")
    List<ParticipationRequestDtoOut> getByEventId(@RequestParam Long eventId);

    @PutMapping("/requests/set-status")
    void setStatusRequests(@RequestParam List<Long> requestIds, @RequestParam Status status);

    @GetMapping("/requests/count-by-event-id")
    List<Object[]> getCountRequestByEventId(@RequestParam List<Long> eventIds,
                                            @RequestParam Status status);
}
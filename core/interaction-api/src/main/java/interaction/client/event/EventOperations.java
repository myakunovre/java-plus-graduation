package interaction.client.event;

import interaction.model.event.output.EventFullDto;
import interaction.model.event.output.EventShortDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface EventOperations {
    @GetMapping("/full-event-by-id")
    EventFullDto getEventFullDtoById(@RequestParam Long eventId);

    @GetMapping("/short-event-by-id")
    EventShortDto getEventShortDtoById(@RequestParam Long eventId);

    @GetMapping("/by-ids")
    List<EventShortDto> getByIds(@RequestParam List<Long> eventIds);
}

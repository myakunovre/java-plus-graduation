package interaction.client;

import interaction.model.request.Status;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestClientFallbackFactory {

    public List<Object[]> getCountRequestByEventId(List<Long> eventIds, Status status) {
        return new ArrayList<>();
    }
}
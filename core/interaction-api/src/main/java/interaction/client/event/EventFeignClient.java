package interaction.client.event;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "event-service", path = "/events")
public interface EventFeignClient extends EventOperations {
}
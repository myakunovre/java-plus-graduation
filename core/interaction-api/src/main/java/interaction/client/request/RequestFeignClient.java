package interaction.client.request;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "request-service", fallbackFactory = RequestClientFallbackFactory.class)
public interface RequestFeignClient extends RequestOperations {
}
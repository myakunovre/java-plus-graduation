package interaction.client.user;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserFeignClient extends UserOperations {
}
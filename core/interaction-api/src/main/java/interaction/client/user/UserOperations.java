package interaction.client.user;

import interaction.model.user.output.UserShortDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserOperations {

    @GetMapping("/{id}")
    UserShortDto getById(@PathVariable("id") Long id);

    @GetMapping("/by-ids")
    List<UserShortDto> getByIds(@RequestParam List<Long> userIds);
}

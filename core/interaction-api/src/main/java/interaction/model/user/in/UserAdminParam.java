package interaction.model.user.in;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserAdminParam {
    private List<Long> ids;
    private int from;
    private int size;
}
package interaction.model.comment.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentAdminParam {
    Integer from = 0;
    Integer size = 10;
    StateFilter status;
    LocalDateTime start;
    LocalDateTime end;
}
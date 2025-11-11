package interaction.model.comment.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCommentParam {
    Long userId;
    Integer from;
    Integer size;
    StateFilter status;
}
package interaction.model.comment.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import interaction.model.user.output.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import interaction.model.event.output.EventShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentShortDto {
    Long id;
    String text;
    EventShortDto event;
    UserShortDto author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
}
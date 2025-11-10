package interaction.model.comment.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import interaction.model.user.output.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import interaction.model.event.output.EventShortDto;
import interaction.model.event.State;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentFullDto {
    Long id;
    String text;
    EventShortDto event;
    UserShortDto author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime modifiedOn;
    State state;
}
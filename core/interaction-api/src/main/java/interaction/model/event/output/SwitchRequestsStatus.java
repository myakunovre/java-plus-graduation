package interaction.model.event.output;

import interaction.model.request.ParticipationRequestDtoOut;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SwitchRequestsStatus {
    List<ParticipationRequestDtoOut> confirmedRequests;
    List<ParticipationRequestDtoOut> rejectedRequests;
}

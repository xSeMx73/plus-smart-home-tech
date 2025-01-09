package hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {
    @NotBlank
    private String sensorId;
    @NotBlank
    private ActionType type;
    @NotBlank
    private Integer value;
}

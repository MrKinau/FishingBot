package systems.kinau.fishingbot.ejection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LookEjectFunction {

    private float yaw;
    private float pitch;
    private int speed;
    private short slot;
}

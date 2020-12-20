package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class LookEjectFunction {

    private float yaw;
    private float pitch;
    private int speed;
    private Consumer<Boolean> onFinish;
    private short slot;
}

package systems.kinau.fishingbot.modules.timer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Data
public class Timer {

    private String name;
    private int units;
    private TimeUnit timeUnit;
    private List<String> commands;

}

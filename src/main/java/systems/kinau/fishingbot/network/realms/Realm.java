package systems.kinau.fishingbot.network.realms;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Realm {

    private long id;
    private String name;
    private String owner;
    private String motd;
}

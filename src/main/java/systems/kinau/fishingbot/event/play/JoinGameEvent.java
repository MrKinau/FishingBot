/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class JoinGameEvent extends Event {

    @Getter private int eid;
    @Getter private int gamemode;
    @Getter private String[] worldIdentifier;
    @Getter private String dimension;
    @Getter private String spawnWorld;
    @Getter private long hashedSeed;
    @Getter private int difficulty;
    @Getter private int maxPlayers;
    @Getter private int viewDistance;
    @Getter private String levelType;
    @Getter private boolean reducedDebugInfo;
    @Getter private boolean enableRespawnScreen;
    @Getter private boolean debug;
    @Getter private boolean flat;

}

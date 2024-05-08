/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class JoinGameEvent extends Event {

    private int eid;
    private int gamemode;
    private String[] worldIdentifier;
    private String dimension;
    private String spawnWorld;
    private long hashedSeed;
    private int difficulty;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private String levelType;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean debug;
    private boolean flat;

}

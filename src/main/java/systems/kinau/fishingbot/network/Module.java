/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;

@AllArgsConstructor
public abstract class Module {

    @Getter private NetworkHandler networkHandler;

    public abstract void perform();
}

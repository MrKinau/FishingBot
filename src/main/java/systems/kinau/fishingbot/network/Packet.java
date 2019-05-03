/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public abstract class Packet {

    public abstract void write(ByteArrayDataOutput out) throws IOException;

    public abstract void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException;

}

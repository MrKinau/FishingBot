package systems.kinau.fishingbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.login.LoginPluginRequestEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class PacketInLoginPluginRequest extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int msgId = readVarInt(in);
        String channel = readString(in);
        byte[] data = new byte[in.getAvailable()];
        in.readFully(data);
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new LoginPluginRequestEvent(msgId, channel, data));
    }
}

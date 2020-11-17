package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.ConfirmTransactionEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
public class PacketInConfirmTransaction extends Packet {

    @Getter private byte windowId;
    @Getter private short action;
    @Getter private boolean accepted;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.windowId = in.readByte();
        this.action = in.readShort();
        this.accepted = in.readBoolean();

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ConfirmTransactionEvent(getWindowId(), getAction(), isAccepted()));
    }
}

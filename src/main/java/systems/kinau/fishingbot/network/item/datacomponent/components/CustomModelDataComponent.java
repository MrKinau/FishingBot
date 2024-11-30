package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;

public class CustomModelDataComponent extends DataComponent {

    private int value;

    private List<Float> floats;
    private List<Boolean> flags;
    private List<String> strings;
    private List<Integer> colors;

    public CustomModelDataComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_4_RC_3) {
            Packet.writeVarInt(value, out);
        } else {
            Packet.writeVarInt(floats.size(), out);
            for (Float value : floats) {
                out.writeFloat(value);
            }
            Packet.writeVarInt(flags.size(), out);
            for (Boolean flag : flags) {
                out.writeBoolean(flag);
            }
            Packet.writeVarInt(strings.size(), out);
            for (String string : strings) {
                Packet.writeString(string, out);
            }
            Packet.writeVarInt(colors.size(), out);
            for (Integer color : colors) {
                out.writeInt(color);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_4_RC_3) {
            this.value = Packet.readVarInt(in);
        } else {
            int count = Packet.readVarInt(in);
            this.floats = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                floats.add(in.readFloat());
            }

            count = Packet.readVarInt(in);
            this.flags = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                flags.add(in.readBoolean());
            }

            count = Packet.readVarInt(in);
            this.strings = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                strings.add(Packet.readString(in));
            }

            count = Packet.readVarInt(in);
            this.colors = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                colors.add(in.readInt());
            }
        }
    }

    @Override
    public String toString(int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_4_RC_3) {
            return super.toString(protocolId) + "[value=" + value + "]";
        } else {
            return super.toString(protocolId) + "[floats=" + floats + ",flags=" + flags + ",strings=" + strings + ",colors=" + colors + "]";
        }
    }
}

package systems.kinau.fishingbot.utils.nbt;

import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class NBTTag {

    private final Tag<?> tag;
    private final byte[] data;

    public NBTTag(ByteArrayDataInputWrapper in, int protocol) {
        ByteArrayDataInputWrapper clone = in.clone();
        int startAvailable = in.getAvailable();
        byte type = in.readByte();
        if (protocol >= ProtocolConstants.MINECRAFT_1_20_2) {
            this.tag = TagRegistry.createTag(type).read(in);
        } else {
            this.tag = TagRegistry.createTag(type).readNamed(in);
        }
        int bytes = startAvailable - in.getAvailable();
        this.data = new byte[bytes];
        clone.readBytes(data);
    }
}

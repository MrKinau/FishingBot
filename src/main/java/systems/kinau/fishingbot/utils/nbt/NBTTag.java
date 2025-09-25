package systems.kinau.fishingbot.utils.nbt;

import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Getter
public class NBTTag {

    private Tag<?> tag;
    private byte[] data;

    public NBTTag(ByteArrayDataInputWrapper in, int protocol) {
        readUncompressed(in, protocol);
    }

    public NBTTag(GZIPInputStream gzipInputStream, int protocol) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];

            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            gzipInputStream.close();
            out.close();
        }

        ByteArrayDataInputWrapper data = new ByteArrayDataInputWrapper(out.toByteArray());
        readUncompressed(data, protocol);
    }

    private void readUncompressed(ByteArrayDataInputWrapper in, int protocol) {
        ByteArrayDataInputWrapper clone = in.clone();
        int startAvailable = in.getAvailable();
        byte type = in.readByte();
        if (protocol >= ProtocolConstants.MC_1_20_2) {
            this.tag = TagRegistry.createTag(type).read(in);
        } else {
            this.tag = TagRegistry.createTag(type).readNamed(in);
        }
        int bytes = startAvailable - in.getAvailable();
        this.data = new byte[bytes];
        clone.readBytes(data);
    }

    @Override
    public String toString() {
        return "\n" + getTag().toString();
    }
}

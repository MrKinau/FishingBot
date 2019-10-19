/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package systems.kinau.fishingbot.network.protocol;


import com.google.common.io.CharStreams;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.play.PacketOutLook;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPlayer;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosLook;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosition;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Replayer {

    @Getter private List<byte[]> path = new ArrayList<>();

    public Replayer(String resource) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);

            String text;
            try (final Reader reader = new InputStreamReader(inputStream)) {
                text = CharStreams.toString(reader);
            }

            String[] lines = text.split("\n");
            for (String line : lines) {
                byte[] lineData = new byte[256];
                String[] lineParts = line.split(";");
                for (int i = 0; i < 256; i++) {
                    lineParts[i] = lineParts[i].replace("\n", "").replace("\r", "");
                    lineData[i] = Byte.parseByte(lineParts[i]);
                }
                path.add(lineData);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void replay() {
        getPath().forEach(bytes -> {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int id = buffer.getInt();
            System.out.println(id);
            switch (id) {
                case 15: {
                    float yaw = buffer.getFloat();
                    float pitch = buffer.getFloat();
                    boolean onGround = buffer.get() != 0;
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutLook(yaw, pitch, onGround));
                    break;
                }
                case 14: {
                    double x = buffer.getDouble();
                    double y = buffer.getDouble();
                    double z = buffer.getDouble();
                    float yaw = buffer.getFloat();
                    float pitch = buffer.getFloat();
                    boolean onGround = buffer.get() != 0;
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutPosLook(x, y, z, yaw, pitch, onGround));
                    break;
                }
                case 13: {
                    double x = buffer.getDouble();
                    double y = buffer.getDouble();
                    double z = buffer.getDouble();
                    boolean onGround = buffer.get() != 0;
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutPosition(x, y, z, onGround));
                    break;
                }
                case 12: {
                    boolean onGround = buffer.get() != 0;
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutPlayer(onGround));
                    break;
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}

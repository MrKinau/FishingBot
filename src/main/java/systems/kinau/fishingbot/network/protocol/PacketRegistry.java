/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.utils.InvalidPacketException;

import java.util.LinkedHashMap;
import java.util.Map;

public class PacketRegistry {

    @Getter private Map<Integer, Class<? extends Packet>> registeredPackets = new LinkedHashMap<>();

    public void registerPacket(int id, Class<? extends Packet> clazz) {
        if(registeredPackets.containsKey(id))
            return;
        registeredPackets.put(id, clazz);
    }

    public Class<? extends Packet> getPacket(int id) {
        return registeredPackets.get(id);
    }

    public int getId(Class<? extends Packet> clazz) throws InvalidPacketException {
        final int[] id = {-1};
        registeredPackets.keySet().stream()
                .filter(integer -> registeredPackets.get(integer).getName().equals(clazz.getName()))
                .findFirst()
                .ifPresent(integer -> id[0] = integer);
        if (id[0] == -1) {
            FishingBot.getI18n().severe("network-unknown-packet-id", clazz.getSimpleName(), ProtocolConstants.getVersionString(FishingBot.getInstance().getCurrentBot().getServerProtocol()));
            FishingBot.getInstance().getCurrentBot().setRunning(false);
            FishingBot.getInstance().getCurrentBot().setWontConnect(true);
            throw new InvalidPacketException("Packet not registered: " + clazz.getSimpleName());
        }
        return id[0];
    }

    public void copyOf(PacketRegistry packetRegistryBase) {
        packetRegistryBase.getRegisteredPackets().forEach((packetId, packetClass) -> {
            this.registeredPackets.put(packetId, packetClass);
        });
    }
}

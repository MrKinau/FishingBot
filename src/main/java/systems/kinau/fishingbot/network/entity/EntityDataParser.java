package systems.kinau.fishingbot.network.entity;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.EntityDataEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;

public class EntityDataParser {

    private final EntityDataElementRegistry dataElementRegistry;

    public EntityDataParser() {
        this.dataElementRegistry = new EntityDataElementRegistry();
    }

    public void readEntityData(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int protocolId) {
        int entityId = Packet.readVarInt(in);

        try {
            List<EntityDataValue<?>> data = new ArrayList<>();

            while (true) {
                if (in.getAvailable() == 0)
                    break;
                int elementIndex = in.readByte();
                if (protocolId == ProtocolConstants.MC_1_8 && elementIndex == 127)
                    break;
                else if (protocolId != ProtocolConstants.MC_1_8 && (elementIndex == -1 || elementIndex == 127 || in.getAvailable() <= 1))
                    break;

                int elementType;
                if (protocolId == ProtocolConstants.MC_1_8) {
                    elementIndex = elementIndex & 224;
                    elementType = elementIndex >> 5;
                } else {
                    elementType = in.readByte();
                }

                EntityDataElement<?> element = dataElementRegistry.createElement(elementType, protocolId);
                element.read(in, networkHandler, protocolId);

                data.add(new EntityDataValue<>(elementIndex, elementType, element));
                if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
                    FishingBot.getLog().info("Entity data for " + entityId + " is: " + elementIndex + " of type " + elementType + " = " + element.getValue());
            }
            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityDataEvent(entityId, data));
        } catch (Exception ex) {
            if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
                ex.printStackTrace();
        }
    }
}

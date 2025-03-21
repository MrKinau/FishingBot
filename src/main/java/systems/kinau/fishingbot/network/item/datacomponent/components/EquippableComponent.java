package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class EquippableComponent extends DataComponent {

    private int equipmentSlot;
    private SoundEvent equipSound;
    private Optional<String> model = Optional.empty();
    private Optional<String> cameraOverlay = Optional.empty();
    private Optional<HolderSetComponentPart> allowedEntities = Optional.empty();
    private boolean dispensable;
    private boolean swappable;
    private boolean damageOnHurt;
    private boolean equipOnInteract;

    public EquippableComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(equipmentSlot, out);
        equipSound.write(out, protocolId);
        out.writeBoolean(model.isPresent());
        model.ifPresent(s -> Packet.writeString(s, out));
        out.writeBoolean(cameraOverlay.isPresent());
        cameraOverlay.ifPresent(s -> Packet.writeString(s, out));
        out.writeBoolean(allowedEntities.isPresent());
        allowedEntities.ifPresent(holderSetComponentPart -> holderSetComponentPart.write(out, protocolId));
        out.writeBoolean(dispensable);
        out.writeBoolean(swappable);
        out.writeBoolean(damageOnHurt);
        if (protocolId >= ProtocolConstants.MC_1_21_5_RC_1)
            out.writeBoolean(equipOnInteract);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.equipmentSlot = Packet.readVarInt(in);
        this.equipSound = new SoundEvent();
        equipSound.read(in, protocolId);
        if (in.readBoolean())
            this.model = Optional.of(Packet.readString(in));
        else
            this.model = Optional.empty();
        if (in.readBoolean())
            this.cameraOverlay = Optional.of(Packet.readString(in));
        else
            this.cameraOverlay = Optional.empty();
        if (in.readBoolean()) {
            HolderSetComponentPart allowedEntities = new HolderSetComponentPart();
            allowedEntities.read(in, protocolId);
            this.allowedEntities = Optional.of(allowedEntities);
        } else {
            this.allowedEntities = Optional.empty();
        }
        this.dispensable = in.readBoolean();
        this.swappable = in.readBoolean();
        this.damageOnHurt = in.readBoolean();
        if (protocolId >= ProtocolConstants.MC_1_21_5_RC_1)
            this.equipOnInteract = in.readBoolean();
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[equipmentSlot=" + equipmentSlot + ",equipSound=" + equipSound.toString(protocolId) + model.map(s -> ",model=" + s).orElse("") + cameraOverlay.map(string -> ",cameraOverlay=" + string).orElse("") + allowedEntities.map(s -> ",allowedEntities=" + s.toString(protocolId)).orElse("") + ",dispensable=" + dispensable + ",swappable=" + swappable + ",damageOnHurt=" + damageOnHurt + ",equipOnInteract=" + equipOnInteract + "]";
    }
}

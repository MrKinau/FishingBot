package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.blocksattacks.DamageReduction;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.blocksattacks.ItemDamageFunction;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class BlocksAttacksComponent extends DataComponent {

    private float blockDelaySeconds;
    private float disableCooldownScale;
    private List<DamageReduction> damageReductions;
    private ItemDamageFunction itemDamageFunction;
    private Optional<String> optBypassDamageTag;
    private Optional<SoundEvent> optBlockSound;
    private Optional<SoundEvent> optDisableSound;

    public BlocksAttacksComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(blockDelaySeconds);
        out.writeFloat(disableCooldownScale);
        Packet.writeVarInt(damageReductions.size(), out);
        for (DamageReduction damageReduction : damageReductions) {
            damageReduction.write(out, protocolId);
        }
        itemDamageFunction.write(out, protocolId);
        out.writeBoolean(optBypassDamageTag.isPresent());
        optBypassDamageTag.ifPresent(s -> Packet.writeString(s, out));
        out.writeBoolean(optBlockSound.isPresent());
        optBlockSound.ifPresent(soundEvent -> soundEvent.write(out, protocolId));
        out.writeBoolean(optDisableSound.isPresent());
        optDisableSound.ifPresent(soundEvent -> soundEvent.write(out, protocolId));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.blockDelaySeconds = in.readFloat();
        this.disableCooldownScale = in.readFloat();
        int count = Packet.readVarInt(in);
        this.damageReductions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            DamageReduction damageReduction = new DamageReduction();
            damageReduction.read(in, protocolId);
        }
        this.itemDamageFunction = new ItemDamageFunction();
        itemDamageFunction.read(in, protocolId);
        if (in.readBoolean()) {
            this.optBypassDamageTag = Optional.of(Packet.readString(in));
        } else {
            this.optBypassDamageTag = Optional.empty();
        }
        if (in.readBoolean()) {
            SoundEvent soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.optBlockSound = Optional.of(soundEvent);
        } else {
            this.optBlockSound = Optional.empty();
        }
        if (in.readBoolean()) {
            SoundEvent soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.optDisableSound = Optional.of(soundEvent);
        } else {
            this.optDisableSound = Optional.empty();
        }
    }
}

package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.kineticweapon.KineticWeaponCondition;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class KineticWeaponComponent extends DataComponent {

    private int contactCooldownTicks;
    private int delayTicks;
    private Optional<KineticWeaponCondition> dismountConditions;
    private Optional<KineticWeaponCondition> knockbackConditions;
    private Optional<KineticWeaponCondition> damageConditions;
    private float forwardMovement;
    private float damageMultiplier;
    private Optional<SoundEvent> sound;
    private Optional<SoundEvent> hitSound;

    public KineticWeaponComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(contactCooldownTicks, out);
        Packet.writeVarInt(delayTicks, out);
        out.writeBoolean(dismountConditions.isPresent());
        dismountConditions.ifPresent(condition -> condition.write(out, protocolId));
        out.writeBoolean(knockbackConditions.isPresent());
        knockbackConditions.ifPresent(condition -> condition.write(out, protocolId));
        out.writeBoolean(damageConditions.isPresent());
        damageConditions.ifPresent(condition -> condition.write(out, protocolId));
        out.writeFloat(forwardMovement);
        out.writeFloat(damageMultiplier);
        out.writeBoolean(sound.isPresent());
        sound.ifPresent(soundEvent -> soundEvent.write(out, protocolId));
        out.writeBoolean(hitSound.isPresent());
        hitSound.ifPresent(soundEvent -> soundEvent.write(out, protocolId));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.contactCooldownTicks = Packet.readVarInt(in);
        this.delayTicks = Packet.readVarInt(in);
        if (in.readBoolean()) {
            KineticWeaponCondition condition = new KineticWeaponCondition();
            condition.read(in, protocolId);
            this.dismountConditions = Optional.of(condition);
        } else {
            this.dismountConditions = Optional.empty();
        }
        if (in.readBoolean()) {
            KineticWeaponCondition condition = new KineticWeaponCondition();
            condition.read(in, protocolId);
            this.knockbackConditions = Optional.of(condition);
        } else {
            this.knockbackConditions = Optional.empty();
        }
        if (in.readBoolean()) {
            KineticWeaponCondition condition = new KineticWeaponCondition();
            condition.read(in, protocolId);
            this.damageConditions = Optional.of(condition);
        } else {
            this.damageConditions = Optional.empty();
        }
        this.forwardMovement = in.readFloat();
        this.damageMultiplier = in.readFloat();
        if (in.readBoolean()) {
            SoundEvent soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.sound = Optional.of(soundEvent);
        } else {
            this.sound = Optional.empty();
        }
        if (in.readBoolean()) {
            SoundEvent soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.hitSound = Optional.of(soundEvent);
        } else {
            this.hitSound = Optional.empty();
        }
    }
}

package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class PiercingWeaponComponent extends DataComponent {

    private boolean dealsKnockback;
    private boolean dismounts;
    private Optional<SoundEvent> sound;
    private Optional<SoundEvent> hitSound;

    public PiercingWeaponComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(dealsKnockback);
        out.writeBoolean(dismounts);
        out.writeBoolean(sound.isPresent());
        sound.ifPresent(soundEvent -> soundEvent.write(out, protocolId));
        out.writeBoolean(hitSound.isPresent());
        hitSound.ifPresent(soundEvent -> soundEvent.write(out, protocolId));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.dealsKnockback = in.readBoolean();
        this.dismounts = in.readBoolean();
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

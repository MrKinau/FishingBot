package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ConsumableComponent extends DataComponent {

    private float consumeSeconds;
    private int animation;
    private InstrumentComponent.SoundEvent sound;
    private boolean consumeParticles;
    private List<ConsumeEffect> consumeEffects = Collections.emptyList();

    public ConsumableComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(consumeSeconds);
        Packet.writeVarInt(animation, out);
        sound.write(out, protocolId);
        out.writeBoolean(consumeParticles);
        Packet.writeVarInt(consumeEffects.size(), out);
        for (ConsumeEffect effect : consumeEffects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.consumeSeconds = in.readFloat();
        this.animation = Packet.readVarInt(in);
        this.sound = new InstrumentComponent.SoundEvent();
        sound.read(in, protocolId);
        this.consumeParticles = in.readBoolean();
        int count = Packet.readVarInt(in);
        this.consumeEffects = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ConsumeEffect consumeEffect = new ConsumeEffect();
            consumeEffect.read(in, protocolId);
            consumeEffects.add(consumeEffect);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ConsumeEffect implements DataComponentPart {

        private int registryId;
        private DataComponentPart effectType;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(registryId, out);
            if (effectType != null) {
                effectType.write(out, protocolId);
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.registryId = Packet.readVarInt(in);
            switch (Registries.CONSUME_EFFECT_TYPE.getConsumeEffectTypeName(registryId, protocolId)) {
                case "minecraft:apply_effects": {
                    this.effectType = new ApplyEffectsConsumeEffectType();
                    break;
                }
                case "minecraft:remove_effects": {
                    this.effectType = new HolderSetComponentPart();
                    break;
                }
                case "minecraft:clear_all_effects": {
                    break;
                }
                case "minecraft:teleport_randomly": {
                    this.effectType = new TeleportRandomlyConsumeEffectType();
                    break;
                }
                case "minecraft:play_sound": {
                    this.effectType = new InstrumentComponent.SoundEvent();
                    break;
                }
                default: {
                    FishingBot.getLog().info("Received unregistered consume_effect_type: " + registryId + "/" + Registries.CONSUME_EFFECT_TYPE.getConsumeEffectTypeName(registryId, protocolId));
                    return;
                }
            }
            if (effectType != null)
                effectType.read(in, protocolId);
        }
    }

    public static class ApplyEffectsConsumeEffectType implements DataComponentPart {

        private List<FoodComponent.PossibleEffect> possibleEffects = Collections.emptyList();

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(possibleEffects.size(), out);
            for (FoodComponent.PossibleEffect effect : possibleEffects) {
                effect.write(out, protocolId);
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.possibleEffects = new LinkedList<>();
            int count = Packet.readVarInt(in);
            for (int i = 0; i < count; i++) {
                FoodComponent.PossibleEffect possibleEffect = new FoodComponent.PossibleEffect();
                possibleEffect.read(in, protocolId);
                possibleEffects.add(possibleEffect);
            }
        }
    }

    public static class HolderSetComponentPart implements DataComponentPart {

        private int id;
        private String resourceLocation;
        private List<Integer> ids = Collections.emptyList();

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(id, out);
            if (id == 0) {
                Packet.writeString(resourceLocation, out);
            } else {
                for (Integer typeId : ids) {
                    Packet.writeVarInt(typeId, out);
                }
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.id = Packet.readVarInt(in);
            if (id == 0) {
                this.resourceLocation = in.readUTF();
            } else {
                this.ids = new ArrayList<>(id - 1);
                for (int i = 0; i < id - 1; i++) {
                    ids.add(Packet.readVarInt(in));
                }
            }
        }
    }

    public static class TeleportRandomlyConsumeEffectType implements DataComponentPart {

        private float diameter;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            out.writeFloat(diameter);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.diameter = in.readFloat();
        }
    }
}

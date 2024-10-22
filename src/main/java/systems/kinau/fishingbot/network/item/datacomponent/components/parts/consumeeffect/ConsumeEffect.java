package systems.kinau.fishingbot.network.item.datacomponent.components.parts.consumeeffect;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class ConsumeEffect implements DataComponentPart {

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
                this.effectType = new SoundEvent();
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

    @Override
    public String toString(int protocolId) {
        return "{registryId=" + registryId + ", effectType=" + effectType.toString(protocolId) + "}";
    }
}
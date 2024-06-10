package systems.kinau.fishingbot.network.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityDataElementRegistry {

    private final Map<ProtocolIdToElementType, Supplier<EntityDataElement<?>>> elements = new HashMap<>();

    public EntityDataElementRegistry() {
        add(protocolId -> 0, simple("byte", ByteArrayDataInputWrapper::readByte));

        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId == ProtocolConstants.MINECRAFT_1_8, 1)
                .build(), simple("short", ByteArrayDataInputWrapper::readShort));
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId == ProtocolConstants.MINECRAFT_1_8, 2)
                .build(), simple("int", ByteArrayDataInputWrapper::readInt));

        add(protocolId -> 1, simple("varint", in -> Packet.readVarInt(in)));
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 2)
                .build(), simple("varlong", Packet::readVarLong));
        add(ProtocolMapperBuilder.create(2)
                .addRule(protocolId -> protocolId == ProtocolConstants.MINECRAFT_1_8, 3)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 3)
                .build(), simple("float", ByteArrayDataInputWrapper::readFloat));
        add(ProtocolMapperBuilder.create(3)
                .addRule(protocolId -> protocolId == ProtocolConstants.MINECRAFT_1_8, 4)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 4)
                .build(), simple("string", Packet::readString));
        add(ProtocolMapperBuilder.create(4)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 5)
                .build(), simple("text_component", Packet::readChatComponent));
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 5)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 6)
                .build(), simple("optional_text_component", (in, protocolId) -> {
                    if (in.readBoolean())
                        return Optional.of(Packet.readChatComponent(in, protocolId));
                    return Optional.empty();
        }));
        add(ProtocolMapperBuilder.create(5)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 6)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 7)
                .build(), simple("slot", (in, networkHandler, protocolId) -> Packet.readSlot(in, protocolId, networkHandler.getDataComponentRegistry())));

        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId == ProtocolConstants.MINECRAFT_1_8, 6)
                .build(), simple("int_vector", in -> {
            int[] vector = new int[3];
            vector[0] = in.readInt();
            vector[1] = in.readInt();
            vector[2] = in.readInt();
            return vector;
        }));

        add(ProtocolMapperBuilder.create(6)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 7)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 8)
                .build(), simple("boolean", ByteArrayDataInputWrapper::readBoolean)); // Boolean
        add(ProtocolMapperBuilder.create(7)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 8)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 9)
                .build(), simple("rotation", in -> {
                    float[] rotation = new float[3];
                    rotation[0] = in.readFloat();
                    rotation[1] = in.readFloat();
                    rotation[2] = in.readFloat();
                    return rotation;
        })); // Rotation
        add(ProtocolMapperBuilder.create(8)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 9)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 10)
                .build(), simple("position", ByteArrayDataInputWrapper::readLong)); // Position
        add(ProtocolMapperBuilder.create(9)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 10)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 11)
                .build(), simple("optional_position", in -> {
                    if (in.readBoolean())
                        return Optional.of(in.readLong());
                    return Optional.empty();
        })); // Optional Position
        add(ProtocolMapperBuilder.create(10)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 11)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 12)
                .build(), simple("direction", in -> Packet.readVarInt(in))); // Direction
        add(ProtocolMapperBuilder.create(11)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 12)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 13)
                .build(), simple("optional_uuid", in -> {
                    if (in.readBoolean())
                        return Optional.of(Packet.readUUID(in));
                    return Optional.empty(); // Optional UUID
        }));
        add(ProtocolMapperBuilder.create(12)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 13)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 14)
                .build(), simple("block_state", in -> Packet.readVarInt(in))); // Block State
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 15)
                .build(), simple("optional_block_state", in -> Packet.readVarInt(in))); // Optional Block State
        add(ProtocolMapperBuilder.create(13)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 14)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 15)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 16)
                .build(), simple("nbt", Packet::readNBT)); // NBT

        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_13, 15)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 16)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 17)
                .build(), simple("particle", this::readParticle)); // Particle
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 18)
                .build(), simple("particles", (in, networkHandler, protocolId) -> {
                    List<Integer> particles = new LinkedList<>();
                    int count = Packet.readVarInt(in);
                    for (int i = 0; i < count; i++) {
                        particles.add(readParticle(in, networkHandler, protocolId));
                    }
                    return particles;
        })); // Particle Array

        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_14, 16)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 17)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 18)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 19)
                .build(), simple("villager_data", in -> {
                    int[] villagerData = new int[3];
                    villagerData[0] = Packet.readVarInt(in);
                    villagerData[1] = Packet.readVarInt(in);
                    villagerData[2] = Packet.readVarInt(in);
                    return villagerData;
        })); // VillagerData
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_14, 17)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 18)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 19)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 20)
                .build(), simple("optional_varint", in -> Packet.readVarInt(in))); // Optional Var Int
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_14, 18)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 19)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 20)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 21)
                .build(), simple("pose", in -> Packet.readVarInt(in))); // Pose

        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19, 19)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 20)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 21)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 22)
                .build(), simple("cat_variant", in -> Packet.readVarInt(in))); // Cat Variant TODO: Not correct for custom cat variants
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 23)
                .build(), simple("wolf_variant", in -> Packet.readVarInt(in))); // Wolf Variant TODO: Not correct for custom wolf variants
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19, 20)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 21)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 22)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 24)
                .build(), simple("frog_variant", in -> Packet.readVarInt(in))); // Frog Variant TODO: Not correct for custom frog variants
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19, 21)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 22)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 23)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 25)
                .build(), simple("optional_global_pos", (in, protocolId) -> {
                    if (protocolId >= ProtocolConstants.MINECRAFT_1_19_4)
                        if (!in.readBoolean()) return Optional.empty();
                    String dimension = Packet.readString(in);
                    long pos = in.readLong();
                    return Optional.of(dimension);
        })); // (Optional) Global Pos
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19, 22)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_3, 23)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 24)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 26)
                .build(), simple("painting_variant", in -> Packet.readVarInt(in))); // Painting Variant TODO: Not correct for custom painting variants

        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 25)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 27)
                .build(), simple("sniffer_state", in -> Packet.readVarInt(in))); // Sniffer State
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 28)
                .build(), simple("armadillo_state", in -> Packet.readVarInt(in))); // Armadillo State
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 26)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 29)
                .build(), simple("vector", in -> {
            float[] vector = new float[3];
            vector[0] = in.readFloat();
            vector[1] = in.readFloat();
            vector[2] = in.readFloat();
            return vector;
        })); // Vector3f
        add(ProtocolMapperBuilder.create(-1)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_19_4, 27)
                .addRule(protocolId -> protocolId >= ProtocolConstants.MINECRAFT_1_20_5, 30)
                .build(), simple("quaternion", in -> {
            float[] vector = new float[4];
            vector[0] = in.readFloat();
            vector[1] = in.readFloat();
            vector[2] = in.readFloat();
            vector[3] = in.readFloat();
            return vector;
        })); // Quaternion4f
    }

    public EntityDataElement<?> createElement(int elementType, int protocolId) {
        return elements.entrySet().stream()
                .filter(protocolIdToElementType -> protocolIdToElementType.getKey().apply(protocolId) == elementType)
                .findAny()
                .map(functionSupplierEntry -> functionSupplierEntry.getValue().get())
                .orElse(null);
    }

    private void add(ProtocolIdToElementType protocolIdToElementType, Function<ProtocolIdToElementType, Supplier<EntityDataElement<?>>> function) {
        elements.put(protocolIdToElementType, function.apply(protocolIdToElementType));
    }

    private <T> Function<ProtocolIdToElementType, Supplier<EntityDataElement<?>>> simple(String internalId, TriFunction<ByteArrayDataInputWrapper, NetworkHandler, Integer, T> readerFunction) {
        return typeId -> () -> new EntityDataElement<>(internalId, readerFunction);
    }

    private <T> Function<ProtocolIdToElementType, Supplier<EntityDataElement<?>>> simple(String internalId, BiFunction<ByteArrayDataInputWrapper, Integer, T> readerFunction) {
        return typeId -> () -> new EntityDataElement<>(internalId, readerFunction);
    }

    private <T> Function<ProtocolIdToElementType, Supplier<EntityDataElement<?>>> simple(String internalId, Function<ByteArrayDataInputWrapper, T> readerFunction) {
        return typeId -> () -> new EntityDataElement<>(internalId, readerFunction);
    }

    private int readParticle(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int protocolId) {
        int particleId = Packet.readVarInt(in);
        String particleName = Registries.PARTICLE_TYPE.getParticleName(particleId, protocolId);
        if (particleName == null)
            return particleId;
        switch (particleName) {
            case "minecraft:entity_effect":
                in.readInt();
                break;
            case "minecraft:block":
            case "minecraft:block_marker":
            case "minecraft:falling_dust":
            case "minecraft:dust_pillar":
                Packet.readVarInt(in); // block
                break;
            case "minecraft:dust":
                in.readFloat(); // color
                in.readFloat(); // color
                in.readFloat(); // color
                in.readFloat(); // scale
                break;
            case "minecraft:dust_color_transition":
                in.readFloat(); // fromColor
                in.readFloat(); // fromColor
                in.readFloat(); // fromColor
                in.readFloat(); // toColor
                in.readFloat(); // toColor
                in.readFloat(); // toColor
                in.readFloat(); // scale
                break;
            case "minecraft:sculk_charge":
                in.readFloat(); // roll
                break;
            case "minecraft:item":
                Packet.readSlot(in, protocolId, networkHandler.getDataComponentRegistry());
                break;
            case "minecraft:vibration":
                int positionSource = Packet.readVarInt(in);
                if (positionSource == 0) { // block
                    in.readLong();
                } else {                   // entity
                    Packet.readVarInt(in); // source entity
                    in.readFloat();        // yOffset
                }
                Packet.readVarInt(in); // arrivalInTicks
                break;
            case "minecraft:shriek":
                Packet.readVarInt(in); // delay
                break;
        }
        return particleId;
    }

    @FunctionalInterface
    public interface ProtocolIdToElementType {
        int apply(Integer protocolId);
    }

    @RequiredArgsConstructor
    public static class ProtocolMapperBuilder {

        private final int defaultElementType;
        private final List<Rule> rules = new LinkedList<>();

        public static ProtocolMapperBuilder create(int defaultElementType) {
            return new ProtocolMapperBuilder(defaultElementType);
        }

        public ProtocolMapperBuilder addRule(Predicate<Integer> protocolPredicate, int elementType) {
            rules.add(0, new Rule(protocolPredicate, elementType));
            return this;
        }

        public ProtocolIdToElementType build() {
            return protocolId -> {
                for (Rule rule : rules) {
                    if (rule.getProtocolPredicate().test(protocolId))
                        return rule.getElementType();
                }
                return defaultElementType;
            };
        }

        @AllArgsConstructor
        @Getter
        static class Rule {
            private Predicate<Integer> protocolPredicate;
            private int elementType;
        }

    }
}

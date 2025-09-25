package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.bot.registry.registries.CommandArgumentTypeRegistry;
import systems.kinau.fishingbot.event.play.CommandsRegisteredEvent;
import systems.kinau.fishingbot.modules.command.brigardier.CommandNodeData;
import systems.kinau.fishingbot.modules.command.brigardier.CommandTree;
import systems.kinau.fishingbot.modules.command.brigardier.argument.BasicArgumentType;
import systems.kinau.fishingbot.modules.command.brigardier.argument.IdentifiableArgumentType;
import systems.kinau.fishingbot.modules.command.brigardier.argument.MessageArgumentType;
import systems.kinau.fishingbot.modules.command.brigardier.argument.RangeArgumentType;
import systems.kinau.fishingbot.modules.command.brigardier.argument.StubArgumentType;
import systems.kinau.fishingbot.modules.command.brigardier.node.ArgumentNode;
import systems.kinau.fishingbot.modules.command.brigardier.node.LiteralNode;
import systems.kinau.fishingbot.modules.command.brigardier.node.Node;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PacketInCommands extends Packet {

    private CommandDispatcher<CommandExecutor> commandDispatcher;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        List<CommandNodeData> nodes = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++) {
                CommandNodeData data = readCommandNode(in, protocolId);
                if (data != null)
                    nodes.add(data);
            }
            int rootIndex = readVarInt(in);

            RootCommandNode<CommandExecutor> rootNode = (RootCommandNode<CommandExecutor>) new CommandTree(nodes).getNode(rootIndex);
            this.commandDispatcher = new CommandDispatcher<>(rootNode);
            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new CommandsRegisteredEvent(commandDispatcher));
        } catch (Exception ignore) {}
    }

    private CommandNodeData readCommandNode(ByteArrayDataInputWrapper in, int protocolId) {
        try {
            byte flags = in.readByte();
            int count = readVarInt(in);
            if (count > in.getAvailable()) return null;
            int[] children = new int[count];
            for (int i = 0; i < children.length; i++) {
                children[i] = readVarInt(in);
            }
            int redirectNode = ((flags & 0x08) != 0) ? readVarInt(in) : 0;
            Node node = readArgumentBuilder(in, flags, protocolId);
            return new CommandNodeData(node, flags, redirectNode, children);
        } catch (Exception ex) {
            return null;
        }
    }

    private Node readArgumentBuilder(ByteArrayDataInputWrapper in, byte flags, int protocolId) {
        int nodeType = flags & 0x03;
        if (nodeType == 2) {
            String name = readString(in);
            int parserId = readVarInt(in);
            IdentifiableArgumentType<?> argumentType = null;
            switch (parserId) {
                case 0: {
                    argumentType = new BasicArgumentType<>(parserId, BoolArgumentType::bool);
                    break;
                }
                case 1: {
                    byte propFlags = in.readByte();
                    float min = (propFlags & 0x01) != 0 ? in.readFloat() : Float.MIN_VALUE;
                    float max = (propFlags & 0x02) != 0 ? in.readFloat() : Float.MAX_VALUE;
                    argumentType = new RangeArgumentType<>(parserId, flags, min, max, () -> FloatArgumentType.floatArg(min, max));
                    break;
                }
                case 2: {
                    byte propFlags = in.readByte();
                    double min = (propFlags & 0x01) != 0 ? in.readDouble() : Double.MIN_VALUE;
                    double max = (propFlags & 0x02) != 0 ? in.readDouble() : Double.MAX_VALUE;
                    argumentType = new RangeArgumentType<>(parserId, flags, min, max, () -> DoubleArgumentType.doubleArg(min, max));
                    break;
                }
                case 3: {
                    byte propFlags = in.readByte();
                    int min = (propFlags & 0x01) != 0 ? in.readInt() : Integer.MIN_VALUE;
                    int max = (propFlags & 0x02) != 0 ? in.readInt() : Integer.MAX_VALUE;
                    argumentType = new RangeArgumentType<>(parserId, flags, min, max, () -> IntegerArgumentType.integer(min, max));
                    break;
                }
                case 4: {
                    byte propFlags = in.readByte();
                    long min = (propFlags & 0x01) != 0 ? in.readLong() : Long.MIN_VALUE;
                    long max = (propFlags & 0x02) != 0 ? in.readLong() : Long.MAX_VALUE;
                    argumentType = new RangeArgumentType<>(parserId, flags, min, max, () -> LongArgumentType.longArg(min, max));
                    break;
                }
                case 5: {
                    int type = readVarInt(in);
                    switch (type) {
                        case 0: {
                            argumentType = new BasicArgumentType<>(parserId, StringArgumentType::word);
                            break;
                        }
                        case 1: {
                            argumentType = new BasicArgumentType<>(parserId, StringArgumentType::string);
                            break;
                        }
                        case 2: {
                            argumentType = new BasicArgumentType<>(parserId, StringArgumentType::greedyString);
                            break;
                        }
                    }
                    break;
                }
                case 6: {
                    byte propFlags = in.readByte();
                    break;
                }
            }
            CommandArgumentTypeRegistry registry = Registries.COMMAND_ARGUMENT_TYPE;
            String registryName = registry.getCommandArgumentTypeName(parserId, protocolId);
            if (registryName != null) {
                if (registryName.equals("minecraft:message"))
                    argumentType = new BasicArgumentType<>(parserId, MessageArgumentType::new);
                else if (registryName.equals("minecraft:score_holder"))
                    in.readByte();
                else if (registryName.equals("minecraft:time"))
                    in.readInt();
                else if (registryName.equals("minecraft:resource_or_tag") || registryName.equals("minecraft:resource_or_tag_key") || registryName.equals("minecraft:resource") || registryName.equals("minecraft:resource_key") || registryName.equals("minecraft:resource_selector"))
                    readString(in);
            }
            if (argumentType == null)
                argumentType = new BasicArgumentType<>(parserId, () -> new StubArgumentType(parserId));
            String identifier = ((flags & 0x10) != 0) ? readString(in) : null;
            return new ArgumentNode<>(name, argumentType, identifier);
        }
        if (nodeType == 1) {
            String name = readString(in);
            return new LiteralNode(name);
        }
        return null;
    }

}

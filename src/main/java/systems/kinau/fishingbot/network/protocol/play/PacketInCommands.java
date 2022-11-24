package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.CommandsRegisteredEvent;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor
public class PacketInCommands extends Packet {

    @Getter
    private CommandDispatcher<CommandExecutor> commandDispatcher;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        List<CommandNodeData> nodes = new ArrayList<>();
        for (int i = 0; i < count; i++)
            nodes.add(readCommandNode(in));
        int rootIndex = readVarInt(in);

        RootCommandNode<CommandExecutor> rootNode = (RootCommandNode<CommandExecutor>) new CommandTree(nodes).getNode(rootIndex);
        this.commandDispatcher = new CommandDispatcher<>(rootNode);
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new CommandsRegisteredEvent(commandDispatcher));
    }

    private CommandNodeData readCommandNode(ByteArrayDataInputWrapper in) {
        byte flags = in.readByte();
        int count = readVarInt(in);
        if (count > in.getAvailable()) return null;
        int[] children = new int[count];
        for (int i = 0; i < children.length; i++) {
            children[i] = readVarInt(in);
        }
        int redirectNode = ((flags & 0x08) != 0) ? readVarInt(in) : 0;
        SuggestableNode suggestableNode = readArgumentBuilder(in, flags);
        return new CommandNodeData(suggestableNode, flags, redirectNode, children);
    }

    private SuggestableNode readArgumentBuilder(ByteArrayDataInputWrapper in, byte flags) {
        int nodeType = flags & 0x03;
        if (nodeType == 2) {
            String name = readString(in);
            int parserId = readVarInt(in);
            ArgumentType<?> argumentType = null;
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
                case 18: {
                    argumentType = new BasicArgumentType<>(parserId, MessageArgumentType::new);
                    break;
                }
                case 6:
                case 29: {
                    byte propFlags = in.readByte();
                    break;
                }
                case 43:
                case 44: {
                    String identifier = readString(in);
                    break;
                }
            }
            if (argumentType == null)
                argumentType = new BasicArgumentType<>(parserId, () -> new UnimplementedArgumentType(parserId));
            String identifier = ((flags & 0x10) != 0) ? readString(in) : null;
            return new ArgumentNode<>(name, argumentType, identifier);
        }
        if (nodeType == 1) {
            String name = readString(in);
            return new LiteralNode(name);
        }
        return null;
    }

    @AllArgsConstructor
    class CommandNodeData {
        private SuggestableNode node;
        private byte flags;
        private int redirectNode;
        private int[] children;
    }

    class CommandTree {

        private final List<CommandNodeData> nodeData;
        private final List<CommandNode<CommandExecutor>> nodes;

        public CommandTree(List<CommandNodeData> nodeData) {
            this.nodeData = nodeData;
            this.nodes = new ArrayList<>(nodeData.size());
            for (int i = 0; i < nodeData.size(); i++)
                this.nodes.add(null);
        }

        public CommandNode<CommandExecutor> getNode(int index) {
            CommandNode<CommandExecutor> node = nodes.get(index);
            if (node != null)
                return node;

            CommandNodeData data = nodeData.get(index);

            if (data.node == null) {
                node = new RootCommandNode<>();
            } else {
                ArgumentBuilder<CommandExecutor, ?> argumentBuilder = data.node.createArgumentBuilder();
                if ((data.flags & 0x08) != 0) {
                    argumentBuilder.redirect(getNode(data.redirectNode));
                }
                if ((data.flags & 0x04) != 0) {
                    argumentBuilder.executes(context -> 0);
                }
                node = argumentBuilder.build();
            }
            nodes.set(index, node);

            for (int i : data.children) {
                CommandNode<CommandExecutor> childNode = getNode(i);
                if (!(childNode instanceof RootCommandNode)) {
                    node.addChild(childNode);
                }
            }
            return node;
        }
    }

    @AllArgsConstructor
    public static abstract class SuggestableNode {
        protected String name;

        public abstract ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder();
    }

    public static class LiteralNode extends SuggestableNode {
        public LiteralNode(String name) {
            super(name);
        }

        @Override
        public ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder() {
            return LiteralArgumentBuilder.literal(name);
        }
    }

    @RequiredArgsConstructor
    public static abstract class ArgumentType<T extends com.mojang.brigadier.arguments.ArgumentType<?>> {
        private final int id;

        public abstract T createType();
    }

    public static class BasicArgumentType<T extends com.mojang.brigadier.arguments.ArgumentType<?>> extends ArgumentType<T> {

        protected Supplier<T> supplier;

        public BasicArgumentType(int id, Supplier<T> supplier) {
            super(id);
            this.supplier = supplier;
        }

        @Override
        public T createType() {
            return supplier.get();
        }
    }

    public static class MessageArgumentType implements com.mojang.brigadier.arguments.ArgumentType<String> {

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            StringBuilder builder = new StringBuilder();
            while (reader.canRead())
                builder.append(reader.read());
            return builder.toString();
        }
    }

    public static class RangeArgumentType<T extends com.mojang.brigadier.arguments.ArgumentType<?>, D> extends BasicArgumentType<T> {
        private final byte flags;
        private final D min;
        private final D max;

        public RangeArgumentType(int id, byte flags, D min, D max, Supplier<T> supplier) {
            super(id, supplier);
            this.flags = flags;
            this.min = min;
            this.max = max;
        }

    }


    public static class UnimplementedArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Object> {

        @Getter
        private int id;
        private Consumer<StringReader> reader;

        public UnimplementedArgumentType(int id) {
            this.id = id;
            this.reader = stringReader -> {
                while (stringReader.canRead() && !Character.isWhitespace(stringReader.peek()))
                    stringReader.skip();
            };
        }

        public UnimplementedArgumentType(int id, Consumer<StringReader> reader) {
            this.id = id;
            this.reader = reader;
        }

        @Override
        public Object parse(StringReader reader) throws CommandSyntaxException {
            this.reader.accept(reader);
            return null;
        }

    }

    public static class ArgumentNode<T extends com.mojang.brigadier.arguments.ArgumentType<?>> extends SuggestableNode {
        private ArgumentType<T> argumentType;
        private String identifier;

        public ArgumentNode(String name, ArgumentType<T> argumentType, String identifier) {
            super(name);
            this.argumentType = argumentType;
            this.identifier = identifier;
        }

        @Override
        public ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder() {
            com.mojang.brigadier.arguments.ArgumentType<?> type = argumentType.createType();
            RequiredArgumentBuilder<CommandExecutor, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(name, type);
//            if (identifier != null)
//                requiredArgumentBuilder.suggests(SuggestionProviders.byId(this.id));
            return requiredArgumentBuilder;
        }
    }
}

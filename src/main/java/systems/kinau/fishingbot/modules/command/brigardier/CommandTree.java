package systems.kinau.fishingbot.modules.command.brigardier;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandTree {

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

        if (data.getNode() == null) {
            node = new RootCommandNode<>();
        } else {
            ArgumentBuilder<CommandExecutor, ?> argumentBuilder = data.getNode().createArgumentBuilder();
            if ((data.getFlags() & 0x08) != 0) {
                argumentBuilder.redirect(getNode(data.getRedirectNode()));
            }
            if ((data.getFlags() & 0x04) != 0) {
                argumentBuilder.executes(context -> 0);
            }
            node = argumentBuilder.build();
        }
        nodes.set(index, node);

        for (int i : data.getChildren()) {
            CommandNode<CommandExecutor> childNode = getNode(i);
            if (!(childNode instanceof RootCommandNode)) {
                node.addChild(childNode);
            }
        }
        return node;
    }
}

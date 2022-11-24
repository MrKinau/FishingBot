package systems.kinau.fishingbot.modules.command.brigardier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.modules.command.brigardier.node.Node;

@AllArgsConstructor
@Getter
public class CommandNodeData {
    private Node node;
    private byte flags;
    private int redirectNode;
    private int[] children;
}
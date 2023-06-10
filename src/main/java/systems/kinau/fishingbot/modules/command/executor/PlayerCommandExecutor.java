package systems.kinau.fishingbot.modules.command.executor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCommandExecutor implements CommandExecutor {

    private String privateMsgPlayer;

    public boolean shouldAnswerPrivate() {
        return privateMsgPlayer != null;
    }

    @Override
    public CommandExecutionType getType() {
        return shouldAnswerPrivate() ? CommandExecutionType.OTHER_PLAYER_PRIVATE : CommandExecutionType.OTHER_PLAYER;
    }

    @Override
    public void sendMessage(String message) {
        if (getType() == CommandExecutionType.OTHER_PLAYER_PRIVATE) {
            FishingBot.getInstance().getCurrentBot().getPlayer().sendMessage("/msg " + getPrivateMsgPlayer() + " " + message, this);
        } else {
            FishingBot.getInstance().getCurrentBot().getPlayer().sendMessage(message, this);
        }
    }
}

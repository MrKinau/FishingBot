package systems.kinau.fishingbot.event.play;

import com.mojang.brigadier.CommandDispatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

@AllArgsConstructor
public class CommandsRegisteredEvent extends Event {

    @Getter private CommandDispatcher<CommandExecutor> commandDispatcher;

}

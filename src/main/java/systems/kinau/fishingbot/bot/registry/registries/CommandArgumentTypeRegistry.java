package systems.kinau.fishingbot.bot.registry.registries;

import systems.kinau.fishingbot.bot.registry.MetaRegistry;
import systems.kinau.fishingbot.bot.registry.RegistryLoader;

import java.util.Optional;

public class CommandArgumentTypeRegistry extends MetaRegistry<Integer, String> {

    public CommandArgumentTypeRegistry() {
        load(RegistryLoader.simple("minecraft:command_argument_type"));
    }

    public int getCommandArgumentTypeId(String commandArgumentTypeName, int protocol) {
        return Optional.ofNullable(findKey(commandArgumentTypeName, protocol)).orElse(0);
    }

    public String getCommandArgumentTypeName(int commandArgumentTypeId, int protocol) {
        return getElement(commandArgumentTypeId, protocol);
    }
}

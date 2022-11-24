package systems.kinau.fishingbot.modules.command.brigardier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class MessageArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead())
            builder.append(reader.read());
        return builder.toString();
    }
}
package systems.kinau.fishingbot.modules.command.brigardier.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IdentifiableArgumentType<T extends ArgumentType<?>> {

    @Getter protected final int id;

    public abstract T createType();
}

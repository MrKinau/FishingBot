package systems.kinau.fishingbot.modules.command.brigardier.argument;

import com.mojang.brigadier.arguments.ArgumentType;

import java.util.function.Supplier;

public class RangeArgumentType<T extends ArgumentType<?>, D> extends BasicArgumentType<T> {

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
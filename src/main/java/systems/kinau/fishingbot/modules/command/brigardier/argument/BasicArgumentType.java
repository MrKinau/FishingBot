package systems.kinau.fishingbot.modules.command.brigardier.argument;

import com.mojang.brigadier.arguments.ArgumentType;

import java.util.function.Supplier;

public class BasicArgumentType<T extends ArgumentType<?>> extends IdentifiableArgumentType<T> {

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
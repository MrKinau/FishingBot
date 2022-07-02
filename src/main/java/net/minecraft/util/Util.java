package net.minecraft.util;

import java.util.function.LongSupplier;

public class Util {
    public static LongSupplier nanoTimeSupplier = System::nanoTime;

    public static long getMeasuringTimeMs() {
        return getMeasuringTimeNano() / 1000000L;
    }

    public static long getMeasuringTimeNano() {
        return nanoTimeSupplier.getAsLong();
    }
}

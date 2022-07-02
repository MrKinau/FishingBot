package net.minecraft.client.util;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Session {
    public static enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang"),
        MSA("msa");

        private static final Map<String, AccountType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((type) -> type.name, Function.identity()));
        private final String name;

        private AccountType(String name) {
            this.name = name;
        }

        @Nullable
        public static AccountType byName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }
    }
}

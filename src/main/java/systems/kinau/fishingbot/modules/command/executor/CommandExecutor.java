package systems.kinau.fishingbot.modules.command.executor;

import systems.kinau.fishingbot.FishingBot;

public interface CommandExecutor {

    CommandExecutionType getType();

    void sendMessage(String message);

    default void sendTranslatedMessages(String key, Object... args) {
        sendMessage(FishingBot.getI18n().t(key, args));
    }
}

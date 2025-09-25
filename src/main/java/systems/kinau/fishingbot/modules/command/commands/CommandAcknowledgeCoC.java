package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.Bot;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;
import systems.kinau.fishingbot.network.protocol.ProtocolState;
import systems.kinau.fishingbot.network.protocol.configuration.PacketOutCodeOfConduct;

import java.util.function.Predicate;

public class CommandAcknowledgeCoC extends BrigardierCommand {

    public CommandAcknowledgeCoC() {
        super("acknowledgecoc", FishingBot.getI18n().t("command-ackowledgecoc-desc"), "acknowledgecodeofconduct", "acceptcoc", "acceptcodeofconduct");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        Predicate<CommandExecutor> acceptCoc = commandExecutor -> {
            Bot bot = FishingBot.getInstance().getCurrentBot();
            if (bot == null || bot.getNet() == null || bot.getNet().getState() != ProtocolState.CONFIGURATION) {
                commandExecutor.sendTranslatedMessages("command-ackowledgecoc-no-coc");
                return false;
            }
            commandExecutor.sendTranslatedMessages("acknowledged-code-of-conduct");
            bot.getNet().sendPacket(new PacketOutCodeOfConduct());
            return true;
        };

        builder.then(literal("save")
                        .executes(context -> {
                            if (acceptCoc.test(context.getSource())) {
                                FishingBot.getInstance().getConfig().setLatestCodeOfConduct(FishingBot.getInstance().getCurrentBot().getCurrentCodeOfConduct());
                                FishingBot.getInstance().getConfig().save();
                                FishingBot.getInstance().getCurrentBot().getConfig().setLatestCodeOfConduct(FishingBot.getInstance().getCurrentBot().getCurrentCodeOfConduct());
                                FishingBot.getInstance().getCurrentBot().getConfig().save();
                            }
                            return 0;
                        }))
                .executes(context -> {
                    acceptCoc.test(context.getSource());
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-ackowledgecoc-syntax", label);
    }
}

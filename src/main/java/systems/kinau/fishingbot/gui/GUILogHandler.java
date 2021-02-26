package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.io.logging.LogFormatter;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GUILogHandler extends Handler {

    private final TextArea logWindow;
    private final LogFormatter logFormatter;

    public GUILogHandler(TextArea logWindow) {
        this.logWindow = logWindow;
        this.logFormatter = new LogFormatter();
    }

    @Override
    public synchronized void publish(LogRecord record) {
        Platform.runLater(() -> {
            logWindow.appendText(logFormatter.format(record));

            String[] lines = logWindow.getText().split("\n");

            if (FishingBot.getInstance().getCurrentBot() == null) {
                this.logWindow.setText(String.join("\n", lines) + "\n");
            } else if (lines.length > FishingBot.getInstance().getCurrentBot().getConfig().getGuiConsoleMaxLines()) {
                double topScroll = this.logWindow.getScrollTop();
                int delta = lines.length - FishingBot.getInstance().getCurrentBot().getConfig().getGuiConsoleMaxLines();
                lines = Arrays.copyOfRange(lines, delta, lines.length);

                this.logWindow.setText(String.join("\n", lines) + "\n");
                this.logWindow.setScrollTop(topScroll);
            }
        });
    }

    @Override
    public void flush() {
        // Flush not implemented
    }

    @Override
    public void close() throws SecurityException {
        // Close not implemented
    }
}

package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import systems.kinau.fishingbot.io.LogFormatter;

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
        Platform.runLater(() -> logWindow.appendText(logFormatter.format(record)));
    }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }
}

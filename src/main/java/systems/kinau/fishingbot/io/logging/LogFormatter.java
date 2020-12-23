/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.io.logging;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
    private final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

    @Override
    public synchronized String format(LogRecord lr) {
        return String.format(FORMAT,
                new Date(lr.getMillis()),
                lr.getLevel().getLocalizedName(),
                lr.getMessage()
        );
    }
}

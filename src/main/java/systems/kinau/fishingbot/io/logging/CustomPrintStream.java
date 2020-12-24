package systems.kinau.fishingbot.io.logging;

import java.io.PrintStream;
import java.util.logging.Logger;

/**
 * src: https://stackoverflow.com/a/32411461
 */
public class CustomPrintStream extends PrintStream {

    private static final PrintStream originalSystemOut = System.out;
    private static final PrintStream originalSystemErr = System.err;
    private static CustomPrintStream systemOutToLogger;

    private String packageOrClassToLog;
    private Logger logger;

    /**
     * Enable forwarding System.out.println calls to the logger if the stacktrace contains the package parameter
     * @param packageToLog
     */
    public static CustomPrintStream enableForPackage(String packageToLog, Logger logger) {
        systemOutToLogger = new CustomPrintStream(originalSystemOut, packageToLog, logger);
        System.setOut(systemOutToLogger);
        System.setErr(systemOutToLogger);
        return systemOutToLogger;
    }

    /**
     * Disable forwarding to the logger resetting the standard output to the console
     */
    public static void disable() {
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
        systemOutToLogger = null;
    }

    private CustomPrintStream(PrintStream original, String packageOrClassToLog, Logger logger) {
        super(original);
        this.packageOrClassToLog = packageOrClassToLog;
        this.logger = logger;
    }

    @Override
    public void println(String line) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = findCallerToLog(stack);
        if (caller == null) {
            super.println(line);
            return;
        }

        logger.info(line);
    }

    @Override
    public void println(Object x) {
        println(String.valueOf(x));
    }

    public StackTraceElement findCallerToLog(StackTraceElement[] stack) {
        for (StackTraceElement element : stack) {
            if (element.getClassName().startsWith(packageOrClassToLog))
                return element;
        }

        return null;
    }

}

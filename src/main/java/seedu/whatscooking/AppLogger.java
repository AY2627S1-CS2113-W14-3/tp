package seedu.whatscooking;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Supplies the loggers used across the application, configured so that
 * diagnostic output never mixes with what the user sees.
 * <p>
 * Java's default logging setup prints every record at {@link Level#INFO} or
 * above straight to the console. In a console application that would
 * interleave developer diagnostics with the recipe output the user is
 * reading, so the console handlers are detached and all records are written
 * to a log file instead. The console stays reserved for
 * {@code Ui} output.
 * <p>
 * A simpler alternative is to log only at {@link Level#FINE}, which the
 * default configuration already hides. That was rejected because it leaves no
 * way to record a genuine warning: raising any record to {@code WARNING} would
 * immediately start polluting the user's console again.
 */
public class AppLogger {
    /** File that all log records are appended to, created in the working directory. */
    private static final String LOG_FILE = "whatscooking.log";

    private static boolean isConfigured = false;

    private AppLogger() {
        // Utility class: all members are static, so instances are never needed.
    }

    /**
     * Returns the logger for the given class, configuring application-wide
     * logging on the first call.
     *
     * @param loggingClass the class the returned logger reports for
     * @return a logger that writes to the log file rather than the console
     */
    public static Logger getLogger(Class<?> loggingClass) {
        configureOnce();
        return Logger.getLogger(loggingClass.getName());
    }

    /**
     * Redirects logging away from the console and into the log file. Does
     * nothing after the first call, so repeated {@link #getLogger(Class)}
     * calls do not attach duplicate handlers.
     */
    private static synchronized void configureOnce() {
        if (isConfigured) {
            return;
        }
        isConfigured = true;

        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            rootLogger.addHandler(fileHandler);
            // Records go to a file rather than the console, so keeping FINE
            // detail costs the user nothing and makes the log worth reading.
            rootLogger.setLevel(Level.FINE);
        } catch (IOException e) {
            // The log file is unusable, so silence logging rather than fall back
            // to the console: a broken log must not corrupt the user's output.
            rootLogger.setLevel(Level.OFF);
        }
    }
}

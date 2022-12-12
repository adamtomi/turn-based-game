package com.vamk.tbg.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public final class LogUtil {
    private static final LogFormatter FORMATTER = new LogFormatter("[%s::%s]: %s\n", (format, record) -> format.formatted(
            record.getLoggerName(),
            record.getLevel(),
            record.getMessage()
    ));

    private LogUtil() {}

    /**
     * Creates a new logger with {@link this#FORMATTER} and
     * the name provided by {@link this#loggerName(Class)}.
     *
     * @return The newly created logger
     * @see this#withFormatter(Logger, LogFormatter)
     */
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(loggerName(clazz));
        return withFormatter(logger, FORMATTER);
    }

    /**
     * Sets the specified formatter to be the only active
     * formatter for the specified logger.
     *
     * @return The specified logger
     */
    public static Logger withFormatter(Logger logger, LogFormatter formatter) {
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);

        return logger;
    }

    /**
     * Retrieves the simple name of the provided class.
     * The difference between this method and
     * {@link Class#getSimpleName()} is that this takes
     * nested classes into account. So if class A has
     * a nested class B, loggerName(B.class) will return
     * "A$B" whereas {@link Class#getSimpleName()} returns
     * simply "B".
     *
     * @param clazz The class
     * @return The retrieved name
     */
    private static String loggerName(Class<?> clazz) {
        String[] path = clazz.getName().split("\\.");
        return path[path.length - 1];
    }
}

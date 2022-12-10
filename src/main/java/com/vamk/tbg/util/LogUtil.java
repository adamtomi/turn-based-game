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

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(loggerName(clazz));
        return withFormatter(logger, FORMATTER);
    }

    public static Logger withFormatter(Logger logger, LogFormatter formatter) {
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);

        return logger;
    }

    private static String loggerName(Class<?> clazz) {
        String[] path = clazz.getName().split("\\.");
        return path[path.length - 1];
    }
}

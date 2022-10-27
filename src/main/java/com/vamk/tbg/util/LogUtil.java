package com.vamk.tbg.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LogUtil {

    private LogUtil() {}

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getSimpleName());
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(LogFormatter.INSTANCE);
        logger.addHandler(handler);

        return logger;
    }

    /* A simple log message formatter class */
    private static final class LogFormatter extends SimpleFormatter {
        private static final LogFormatter INSTANCE = new LogFormatter();
        private static final String FORMAT = "[%s::%s]: %s\n";

        @Override
        public String format(LogRecord record) {
            return FORMAT.formatted(
                    record.getLoggerName(),
                    record.getLevel(),
                    record.getMessage()
            );
        }
    }
}

package com.vamk.tbg.util;

import java.util.function.BiFunction;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * A simple log formatter class. The provided function
 * will be responsible for the actual formatting, this
 * class just extends {@link SimpleFormatter} so that
 * it can be passed to a {@link java.util.logging.Logger}.
 */
public class LogFormatter extends SimpleFormatter {
    private final String format;
    private final BiFunction<String, LogRecord, String> formatter;

    public LogFormatter(String format, BiFunction<String, LogRecord, String> formatter) {
        this.format = format;
        this.formatter = formatter;
    }

    @Override
    public String format(LogRecord record) {
        return this.formatter.apply(this.format, record);
    }
}

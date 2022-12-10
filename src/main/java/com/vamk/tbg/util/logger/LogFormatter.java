package com.vamk.tbg.util.logger;

import java.util.function.BiFunction;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

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

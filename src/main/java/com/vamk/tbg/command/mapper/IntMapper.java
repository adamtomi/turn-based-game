package com.vamk.tbg.command.mapper;

/**
 * A mapper that converts strings into ints.
 */
public class IntMapper implements ArgumentMapper<Integer> {

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    /**
     * If the specified string is not a valid number,
     * an exception is thrown.
     */
    @Override
    public Integer map(String arg) throws ArgumentException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new ArgumentException(this, arg);
        }
    }
}

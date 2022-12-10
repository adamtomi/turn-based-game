package com.vamk.tbg.command.mapper;

public class IntMapper implements ArgumentMapper<Integer> {

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    public Integer map(String arg) throws ArgumentException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new ArgumentException(this, arg);
        }
    }
}

package com.vamk.tbg.command.mapper;

public interface ArgumentMapper<T> {

    Class<T> type();

    T map(String arg) throws ArgumentException;
}

package com.vamk.tbg.command.mapper;

import com.vamk.tbg.command.CommandException;

public interface ArgumentMapper<T> {

    T map(String arg) throws CommandException;
}

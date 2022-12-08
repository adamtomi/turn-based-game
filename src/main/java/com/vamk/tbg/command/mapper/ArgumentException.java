package com.vamk.tbg.command.mapper;

import com.vamk.tbg.command.CommandException;

import java.io.Serial;

public class ArgumentException extends CommandException {
    private static final String FAILURE_MESSAGE = "Argument '%s' cannot be converted to a(n) %s";
    @Serial
    private static final long serialVersionUID = -7155880067762760763L;

    public ArgumentException(ArgumentMapper<?> mapper, String arg) {
        super(FAILURE_MESSAGE.formatted(arg, mapper.type().getSimpleName()));
    }
}

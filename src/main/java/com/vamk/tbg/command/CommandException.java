package com.vamk.tbg.command;

import java.io.Serial;

public class CommandException extends Exception {
    @Serial
    private static final long serialVersionUID = -6681701613013042999L;

    public CommandException(String message) {
        super(message);
    }
}

package com.vamk.tbg.command;

@FunctionalInterface
public interface CommandExecutable {

    void execute(CommandContext context) throws CommandException;
}

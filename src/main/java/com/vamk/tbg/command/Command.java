package com.vamk.tbg.command;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public interface Command extends CommandExecutable {

    String getName();

    String getDescription();

    List<Argument> getArguments();

    static Builder forName(String name) {
        return new Builder(name);
    }

    final class Builder {
        private final String name;
        private String description;
        private List<Argument> arguments;
        private CommandExecutable executor;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withArguments(Argument.Builder... arguments) {
            this.arguments = Arrays.stream(arguments).map(Argument.Builder::build).toList();
            return this;
        }

        public Builder execute(CommandExecutable executor) {
            this.executor = executor;
            return this;
        }

        public Command build() {
            requireNonNull(this.description, "description cannot be null");
            requireNonNull(this.executor, "executor cannot be null");

            List<Argument> arguments = this.arguments == null ? List.of() : this.arguments;
            return new CommandImpl(this.name, this.description, arguments, this.executor);
        }
    }
}

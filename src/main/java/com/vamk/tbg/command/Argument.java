package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;

import static java.util.Objects.requireNonNull;

public record Argument(String name, String description, boolean required, ArgumentMapper<?> mapper) {

    public static Builder optional(String name) {
        return new Builder(name, false);
    }

    public static Builder required(String name) {
        return new Builder(name, true);
    }

    public static final class Builder {
        private final String name;
        private final boolean required;
        private String description;

        private ArgumentMapper<?> mapper;

        private Builder(String name, boolean required) {
            this.name = name;
            this.required = required;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder useMapper(ArgumentMapper<?> mapper) {
            this.mapper = mapper;
            return this;
        }

        public Argument build() {
            requireNonNull(this.description, "description cannot be null");
            requireNonNull(this.mapper, "mapper cannot be null");

            return new Argument(this.name, this.description, this.required, this.mapper);
        }
    }
}

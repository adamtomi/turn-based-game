package com.vamk.tbg.command;

public class Argument implements CommandPart {
    private final String name;
    private final String description;
    private final boolean optional;

    public Argument(String name, String description, boolean optional) {
        this.name = name;
        this.description = description;
        this.optional = optional;
    }

    public Argument(String name, String description) {
        this(name, description, false);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public String toString() {
        return "Argument { name=%s, description=%s, optional=%b }".formatted(this.name, this.description, this.optional);
    }
}

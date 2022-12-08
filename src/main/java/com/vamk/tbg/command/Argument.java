package com.vamk.tbg.command;

public class Argument implements CommandPart {
    private final String name;
    private final String description;

    public Argument(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "Argument { name=%s, description=%s }".formatted(this.name, this.description);
    }
}

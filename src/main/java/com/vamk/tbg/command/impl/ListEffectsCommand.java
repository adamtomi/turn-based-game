package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ListEffectsCommand extends Command {

    public ListEffectsCommand() {
        super(
                "ls-eff",
                "List status effects of an entity or all of them",
                new Argument("target", "The entity whose active status effects should be displayed", true)
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        if (context.remaining() > 0) {
            Entity target = context.nextArg(Entity.class);
            Set<StatusEffect> effects = target.getEffects().keySet();
            if (effects.isEmpty()) {
                context.respond("Entity %d doesn't have active effects at the moment.".formatted(target.getId()));
            } else {
                String active = target.getEffects()
                        .keySet()
                        .stream()
                        .map(StatusEffect::name)
                        .collect(Collectors.joining(", "));
                context.respond("Entity %d's acitve effects: %s".formatted(target.getId(), active));
            }
        } else {
            String all = Arrays.stream(StatusEffect.values())
                    .map(StatusEffect::name)
                    .collect(Collectors.joining(", "));

            context.respond("Listing all available effects: %s".formatted(all));
        }
    }
}

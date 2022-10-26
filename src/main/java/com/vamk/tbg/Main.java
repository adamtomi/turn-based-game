package com.vamk.tbg;

import com.vamk.tbg.effect.StatusEffect;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new Main().launch();
    }

    private void launch() {
        System.out.println("Preparing game....");
        Random random = new Random();
        Entity player = new Entity(0, false, 100);
        Entity enemy = new Entity(1, true, 100);
        System.out.println("Done!");

        try (Scanner sc = new Scanner(System.in)) {
            while (!player.isDead() && !enemy.isDead()) {
                System.out.println("============================================================");
                System.out.println("Make your move, my friend! (Select an option from below)");
                System.out.println("1) Apply BUFFS to yourself | 2) Damage your enemy");
                try {
                    // Read input from user
                    // -> 1: "perform friendly move"
                    // -> 2: "perform hostile move"
                    int move = sc.nextInt();
                    if (move != 1 && move != 2) {
                        System.out.println("Invalid command, try again");
                        continue;
                    }

                    playRound(player, enemy, move);
                    int enemyCmd = random.nextInt(2) + 1; // Add 1 so that we never hit 0
                    System.out.println(enemyCmd);
                    playRound(enemy, player, enemyCmd);
                } catch (InputMismatchException ex) {
                    System.out.println("Invalid key, try again");
                }
                System.out.println("============================================================");
            }
        }
    }

    private void playRound(Entity friend, Entity enemy, int move) {
        System.out.println("------------------------------------------------------------");
        if (friend.hasEffect(StatusEffect.FROZEN)) {
            System.out.println("Entity %s is frozen...".formatted(friend));
            return;
        }

        if (move == 1) {
            friend.getFriendlyMove().perform(friend);
        } else if (move == 2) {
            if (friend.hasEffect(StatusEffect.LIFESTEAL)) {
                System.out.println("Stealing 10 HP...");
                friend.heal(10);
            }

            friend.getHostileMove().perform(enemy);
        }

        if (friend.hasEffect(StatusEffect.BLEEDING)) {
            System.out.println("Losing 15 HP to bleeding");
            friend.damage(15);
        }

        if (friend.hasEffect(StatusEffect.REGENERATION)) {
            System.out.println("Regenerating 5 HP");
            friend.heal(5);
        }

        if (enemy.isDead()) {
            String message = friend.isHostile() ? "You died!!!" : "Congratulations, you won!";
            shutdown(message);
        }

        if (friend.hasEffect(StatusEffect.CAFFEINATED)) {
            System.out.println("Granting %s another round since they're well caffeinated");
            friend.delEffect(StatusEffect.CAFFEINATED);
            playRound(friend, enemy, move);
        }
        System.out.println("------------------------------------------------------------");
    }

    private void shutdown(String message) {
        System.out.println(message);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {}

        System.exit(0);
    }
}

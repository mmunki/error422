package dev.mmunki.error422.core;

import java.util.List;
import java.util.Random;

public enum Anomaly {
    CHAT_ERROR,
    GLITCH_SCREEN,
    STATIC,
    LIGHTNING,
    NAUSEA,
    PUSH,
    DARKNESS,
    TITLE_GLITCH,
    BLOCK_SWAP,
    INVENTORY_SCRAMBLE,
    FINALE;

    private static final List<Anomaly> CALM = List.of(
            CHAT_ERROR, GLITCH_SCREEN, GLITCH_SCREEN, STATIC);

    private static final List<Anomaly> UNEASY = List.of(
            CHAT_ERROR, GLITCH_SCREEN, GLITCH_SCREEN, STATIC,
            LIGHTNING, NAUSEA, PUSH);

    private static final List<Anomaly> CORRUPT = List.of(
            CHAT_ERROR, GLITCH_SCREEN, GLITCH_SCREEN, STATIC,
            LIGHTNING, NAUSEA, PUSH, DARKNESS, TITLE_GLITCH,
            BLOCK_SWAP, INVENTORY_SCRAMBLE);

    private static final List<Anomaly> FINAL = List.of(
            GLITCH_SCREEN, STATIC, LIGHTNING, PUSH, TITLE_GLITCH);

    public static List<Anomaly> pool(Phase phase) {
        return switch (phase) {
            case CALM -> CALM;
            case UNEASY -> UNEASY;
            case CORRUPT -> CORRUPT;
            case FINAL -> FINAL;
            case ENDED -> List.of();
        };
    }

    public static Anomaly pick(Random random, Phase phase) {
        List<Anomaly> pool = pool(phase);
        if (pool.isEmpty()) {
            return GLITCH_SCREEN;
        }
        return pool.get(random.nextInt(pool.size()));
    }
}

package tw.mmunki.error422.core;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Error422Text {
    private static final List<String> LINES = List.of(
            "java.lang.NullPointerException at net.minecraft.world.World.tick(World.java:422)",
            "java.lang.IllegalStateException: chunk is not loaded",
            "ERR422: session token rejected",
            "unexpected entity at world border",
            "0x45 0x52 0x52 0x34 0x32 0x32",
            "memory corruption in region 0x000001A2",
            "the server is still here",
            "do not look at the debug screen",
            "ERROR422: player state is not valid",
            "packet 0x2E was received twice",
            "net.minecraft.server.MinecraftServer: watchdog timeout",
            "entity id 422 is already in use",
            "world seed mismatch",
            "failed to deserialize player data",
            "connection reset by peer",
            "entity 422 is behind you",
            "do not turn around",
            "the world is still loading you",
            "ERR422 has taken control",
            "player skin is not a player",
            "unknown entity joined the world",
            "horror.minecraft.core is not a valid package",
            "0x45 0x52 0x52 0x30 0x34 0x32 0x32",
            "the code is watching",
            "you were not supposed to see this");

    private static final char[] GLYPHS = "0123456789ABCDEF".toCharArray();

    private Error422Text() {
    }

    public static String line(Random random) {
        return LINES.get(random.nextInt(LINES.size()));
    }

    public static String hex(Random random) {
        StringBuilder builder = new StringBuilder("0x");
        for (int index = 0; index < 8; index++) {
            builder.append(GLYPHS[random.nextInt(GLYPHS.length)]);
        }
        return builder.toString();
    }

    public static String corruption(Random random) {
        StringBuilder builder = new StringBuilder("ERR422 ");
        for (int index = 0; index < 24; index++) {
            builder.append(GLYPHS[random.nextInt(GLYPHS.length)]);
        }
        return builder.toString();
    }

    public static String codeLine(Random random, int length) {
        StringBuilder builder = new StringBuilder(Math.max(1, length));
        for (int index = 0; index < length; index++) {
            builder.append(GLYPHS[random.nextInt(GLYPHS.length)]);
        }
        return builder.toString();
    }

    public static String chatLine(Random random) {
        return switch (random.nextInt(5)) {
            case 0 -> "[ERROR422] " + line(random);
            case 1 -> "[ERROR422] " + hex(random) + " " + hex(random);
            case 2 -> "[ERROR422] " + corruption(random);
            case 3 -> "[ERROR422] entity 422 joined the game";
            default -> "[ERROR422] " + String.format(Locale.ROOT, "tick %d: %s", random.nextInt(99999), line(random));
        };
    }
}

package dev.mmunki.error422.client;

import dev.mmunki.error422.Error422Config;
import dev.mmunki.error422.Error422Settings;
import dev.mmunki.error422.core.Phase;
import dev.mmunki.error422.core.Session;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Error422Overlay {
    private Error422Overlay() {
    }

    public static void render(GuiGraphicsExtractor graphics, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        Session session = Error422Client.session();
        Error422Settings settings = Error422Config.get();
        if (!Error422Client.visualsEnabled() || !settings.enabled || session == null || client.player == null) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        Phase phase = session.phase();
        long time = client.level != null ? client.level.getGameTime() : session.ticks();
        Random random = new Random(Error422Client.visualSeed() ^ (time / 2L));
        int intensity = settings.intensity;

        int scanAlpha = switch (phase) {
            case CALM -> 8;
            case UNEASY -> 16;
            case CORRUPT -> 26;
            case FINAL -> 38;
            case ENDED -> 0;
        };
        for (int y = 0; y < height; y += 3) {
            if (random.nextInt(7) == 0) {
                graphics.fill(0, y, width, y + 1, (scanAlpha << 24) | 0x180000);
            }
        }

        if (Error422Client.glitchTicks() > 0 || phase == Phase.CORRUPT || phase == Phase.FINAL) {
            int bars = 2 + intensity * 2;
            for (int index = 0; index < bars; index++) {
                int y = random.nextInt(Math.max(1, height));
                int barHeight = 2 + random.nextInt(8);
                int red = random.nextInt(0x80);
                graphics.fill(0, y, width, Math.min(height, y + barHeight),
                        (0xAA << 24) | (red << 16));
            }
        }

        Font font = client.font;
        if (phase != Phase.CALM || Error422Client.glitchTicks() > 0) {
            List<String> lines = Error422Client.lines();
            int y = 6;
            for (String line : lines) {
                int jitter = random.nextInt(5) - 2;
                graphics.text(font, line, 6 + jitter, y, 0xFFFF3333);
                y += 10;
                if (y > height - 24) {
                    break;
                }
            }
        }

        if (phase == Phase.FINAL) {
            graphics.fill(0, 0, width, height, (0x55 << 24) | 0x330000);
            graphics.centeredText(font, "ERR422", width / 2, height / 2, 0xFFFF2222);
            graphics.centeredText(font, "the game has taken control",
                    width / 2, height / 2 + 14, 0xFFAA0000);
        }

        if (Error422Client.titleGlitchTicks() > 0) {
            graphics.centeredText(font, "ERR422", width / 2, height / 3, 0xFFFF0000);
            graphics.centeredText(font, "session 0x" + Long.toHexString(Error422Client.visualSeed()).toUpperCase(Locale.ROOT),
                    width / 2, height / 3 + 12, 0xFF880000);
        }

        String status = String.format(Locale.ROOT, "ERR422 %d%%", (int) (session.progress() * 100.0D));
        graphics.text(font, status, width - font.width(status) - 4, height - 12, 0xFFFF4444);
    }

    public static void renderTitle(GuiGraphicsExtractor graphics) {
        Error422Settings settings = Error422Config.get();
        if (!Error422Client.visualsEnabled() || !settings.enabled || !settings.corruptTitleScreen) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        Random random = new Random(0x422L ^ (System.currentTimeMillis() / 120L));
        for (int y = 0; y < height; y += 4) {
            if (random.nextInt(6) == 0) {
                graphics.fill(0, y, width, y + 1, (0x1C << 24) | 0x220000);
            }
        }
        graphics.text(client.font, "ERR422 0x" + Long.toHexString(random.nextLong()).toUpperCase(Locale.ROOT),
                6, 6, 0xFFFF2222);
        graphics.centeredText(client.font, "the game is not ready",
                width / 2, height - 24, 0xFFAA0000);
    }
}

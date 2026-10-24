package tw.mmunki.error422.client;

import tw.mmunki.error422.Error422Config;
import tw.mmunki.error422.Error422Settings;
import tw.mmunki.error422.core.Error422Text;
import tw.mmunki.error422.core.Phase;
import tw.mmunki.error422.core.Session;
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
        if (!session.awakened() && Error422Client.glitchTicks() <= 0) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        Phase phase = session.phase();
        long time = client.level != null ? client.level.getGameTime() : session.ticks();
        Random random = new Random(Error422Client.visualSeed() ^ (time / 2L));
        int intensity = settings.intensity;

        int scanAlpha = switch (phase) {
            case CALM -> 12;
            case UNEASY -> 22;
            case CORRUPT -> 36;
            case FINAL -> 54;
            case ENDED -> 0;
        };
        for (int y = 0; y < height; y += 3) {
            if (random.nextInt(5) == 0) {
                graphics.fill(0, y, width, y + 1, (scanAlpha << 24) | 0x180000);
            }
        }

        int rainAlpha = switch (phase) {
            case CALM -> 24;
            case UNEASY -> 38;
            case CORRUPT -> 62;
            case FINAL -> 92;
            case ENDED -> 0;
        };
        if (phase != Phase.ENDED) {
            renderCodeRain(graphics, client.font, width, height,
                    new Random(Error422Client.visualSeed() ^ (time / 3L)),
                    rainAlpha, 12 + intensity * 5);
        }

        if (Error422Client.glitchTicks() > 0 || phase == Phase.CORRUPT || phase == Phase.FINAL) {
            int bars = 3 + intensity * 3;
            for (int index = 0; index < bars; index++) {
                int y = random.nextInt(Math.max(1, height));
                int barHeight = 2 + random.nextInt(10);
                int red = random.nextInt(0xB0);
                graphics.fill(0, y, width, Math.min(height, y + barHeight),
                        (0xCC << 24) | (red << 16));
            }
        }

        Font font = client.font;
        if (phase != Phase.CALM || Error422Client.glitchTicks() > 0) {
            List<String> lines = Error422Client.lines();
            int y = 6;
            for (String line : lines) {
                int jitter = random.nextInt(7) - 3;
                int color = random.nextBoolean() ? 0xFFFF2222 : 0xFFE0E0E0;
                graphics.text(font, line, 6 + jitter, y, color);
                y += 10;
                if (y > height - 24) {
                    break;
                }
            }
        }

        if (phase == Phase.FINAL || Error422Client.glitchTicks() > 40) {
            graphics.fill(0, 0, width, height, (0x66 << 24) | 0x330000);
            graphics.centeredText(font, "ERR422", width / 2, height / 2, 0xFFFF2222);
            graphics.centeredText(font, "the game has taken control",
                    width / 2, height / 2 + 14, 0xFFAA0000);
            graphics.centeredText(font, Error422Text.codeLine(random, 28),
                    width / 2, height / 2 + 28, 0xFFE0E0E0);
        }

        if (Error422Client.titleGlitchTicks() > 0) {
            graphics.centeredText(font, "ERR422", width / 2, height / 3, 0xFFFF0000);
            graphics.centeredText(font, "session 0x"
                            + Long.toHexString(Error422Client.visualSeed()).toUpperCase(Locale.ROOT),
                    width / 2, height / 3 + 12, 0xFF880000);
        }
    }

    public static void renderTitleBackground(GuiGraphicsExtractor graphics, float partialTick) {
        Error422Settings settings = Error422Config.get();
        if (!Error422Client.visualsEnabled() || !settings.enabled || !settings.corruptTitleScreen) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        long millis = System.currentTimeMillis();
        Random random = new Random(0x422L ^ (millis / 70L));

        graphics.fill(0, 0, width, height, 0xFF030000);
        renderCodeRain(graphics, client.font, width, height,
                new Random(0xE4422L ^ (millis / 55L)), 220, 22);

        for (int index = 0; index < 16; index++) {
            int y = random.nextInt(Math.max(1, height));
            int barHeight = 1 + random.nextInt(9);
            int red = random.nextInt(0xC0);
            graphics.fill(0, y, width, Math.min(height, y + barHeight),
                    (0xCC << 24) | (red << 16));
        }
        for (int y = 0; y < height; y += 4) {
            if (random.nextInt(5) == 0) {
                graphics.fill(0, y, width, y + 1, 0x33000000);
            }
        }
    }

    public static void renderTitle(GuiGraphicsExtractor graphics) {
        Error422Settings settings = Error422Config.get();
        if (!Error422Client.visualsEnabled() || !settings.enabled || !settings.corruptTitleScreen) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        long millis = System.currentTimeMillis();
        Random random = new Random(0x422L ^ (millis / 90L));

        int flashes = 1 + random.nextInt(4);
        for (int index = 0; index < flashes; index++) {
            int x = random.nextInt(Math.max(1, width));
            int y = random.nextInt(Math.max(1, height));
            int chars = 4 + random.nextInt(20);
            graphics.text(client.font, Error422Text.codeLine(random, chars), x, y,
                    random.nextBoolean() ? 0xFFFF2222 : 0xCCE0E0E0);
        }
        if (random.nextInt(4) == 0) {
            graphics.centeredText(client.font, Error422Text.corruption(random),
                    width / 2, height / 2 + 54, 0xAAFF2222);
        }
    }

    public static void renderHorrorLogo(GuiGraphicsExtractor graphics, int width) {
        Error422Settings settings = Error422Config.get();
        if (!Error422Client.visualsEnabled() || !settings.enabled || !settings.corruptTitleScreen) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        int height = graphics.guiHeight();
        long millis = System.currentTimeMillis();
        Random random = new Random(0x484F52L ^ (millis / 110L));
        int jitterX = random.nextInt(5) - 2;
        int jitterY = random.nextInt(3) - 1;
        int y = Math.max(20, height / 5) + jitterY;
        graphics.centeredText(client.font, "HORROR", width / 2 + jitterX, y, 0xFFFF1111);
        graphics.centeredText(client.font, "MINECRAFT", width / 2 - jitterX, y + 18, 0xFFE0E0E0);
        graphics.centeredText(client.font, Error422Text.codeLine(random, 24),
                width / 2, y + 34, 0xAAFF2222);
    }

    private static void renderCodeRain(GuiGraphicsExtractor graphics, Font font,
                                       int width, int height, Random random, int alpha, int density) {
        int step = Math.max(10, 16 - density / 4);
        int columnWidth = 12;
        int colorMask = Math.max(0, Math.min(255, alpha));
        for (int x = 0; x < width; x += columnWidth) {
            int characters = 2 + random.nextInt(Math.max(2, height / step));
            int head = random.nextInt(Math.max(1, height + characters * step));
            for (int index = 0; index < characters; index++) {
                int y = head - index * step;
                if (y < -step || y > height) {
                    continue;
                }
                int fade = Math.max(8, colorMask - index * Math.max(4, colorMask / 12));
                int color = index == 0
                        ? ((Math.min(255, fade + 60) << 24) | 0xFF3030)
                        : ((Math.min(255, fade) << 24) | (index % 3 == 0 ? 0xFF1010 : 0xB02020));
                graphics.text(font, Error422Text.codeLine(random, 2 + random.nextInt(3)), x, y, color);
            }
        }
    }
}

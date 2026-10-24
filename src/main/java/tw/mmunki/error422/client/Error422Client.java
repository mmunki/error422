package tw.mmunki.error422.client;

import com.mojang.blaze3d.platform.InputConstants;
import tw.mmunki.error422.Error422Config;
import tw.mmunki.error422.Error422Settings;
import tw.mmunki.error422.core.Anomaly;
import tw.mmunki.error422.core.Error422Text;
import tw.mmunki.error422.core.Phase;
import tw.mmunki.error422.core.Session;
import tw.mmunki.error422.net.ControlPayload;
import tw.mmunki.error422.entity.Error422Entities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Error422Client implements ClientModInitializer {
    public static final KeyMapping TOGGLE = new KeyMapping(
            "key.error422.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            KeyMapping.Category.MISC);

    private static final List<String> LINES = new ArrayList<>();
    private static Session session;
    private static long visualSeed;
    private static int glitchTicks;
    private static int titleGlitchTicks;
    private static int lineCounter;
    private static boolean visualsEnabled = true;

    @Override
    public void onInitializeClient() {
        Error422Config.load();
        KeyMappingHelper.registerKeyMapping(TOGGLE);
        ModelLayerRegistry.registerModelLayer(Error422EntityRenderer.LAYER,
                () -> LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64));
        EntityRendererRegistry.register(Error422Entities.ERROR422, Error422EntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(ControlPayload.TYPE, (payload, context) ->
                context.client().execute(() -> handlePayload(payload)));
        ClientTickEvents.END_CLIENT_TICK.register(Error422Client::tick);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (Error422Config.get().clientAutoStart && !isActive()) {
                Error422Settings settings = Error422Config.get();
                long seed = settings.seed != 0L ? settings.seed : new Random().nextLong();
                start(seed, settings.sessionSeconds());
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> stop());
    }

    private static void handlePayload(ControlPayload payload) {
        if (payload.action() == ControlPayload.Action.START) {
            start(payload.seed(), payload.durationSeconds());
        } else {
            stop();
        }
    }

    public static void start(long seed, int durationSeconds) {
        session = new Session(seed, Math.max(20, durationSeconds));
        visualSeed = seed;
        glitchTicks = 0;
        titleGlitchTicks = 0;
        lineCounter = 0;
        visualsEnabled = true;
        LINES.clear();
    }

    public static void stop() {
        session = null;
        glitchTicks = 0;
        titleGlitchTicks = 0;
        LINES.clear();
    }

    private static void tick(Minecraft client) {
        boolean toggled = false;
        while (TOGGLE.consumeClick()) {
            toggled = true;
        }
        if (toggled) {
            visualsEnabled = !visualsEnabled;
            if (client.player != null) {
                client.player.sendOverlayMessage(Component.literal(
                        "ERROR422 visuals " + (visualsEnabled ? "on" : "off"))
                        .withStyle(ChatFormatting.RED));
            }
        }

        if (session == null) {
            return;
        }
        if (client.player == null || client.level == null) {
            stop();
            return;
        }

        session.tick();
        for (Anomaly anomaly : session.pollEvents(4)) {
            handle(anomaly, client);
        }
        if (glitchTicks > 0) {
            glitchTicks--;
        }
        if (titleGlitchTicks > 0) {
            titleGlitchTicks--;
        }
        if (session.finished()) {
            stop();
        }
    }

    private static void handle(Anomaly anomaly, Minecraft client) {
        Random random = new Random(visualSeed ^ (session.ticks() * 31L) ^ anomaly.ordinal());
        switch (anomaly) {
            case CHAT_ERROR -> addLine();
            case GLITCH_SCREEN -> {
                glitchTicks = Math.max(glitchTicks, 30 + random.nextInt(50));
                addLine();
            }
            case STATIC -> {
                if (Error422Config.get().playStatic) {
                    play(client, random, 0.9F, SoundEvents.GLASS_BREAK,
                            SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundEvents.AMBIENT_CAVE.value(),
                            SoundEvents.ENDERMAN_SCREAM);
                }
            }
            case WHISPER -> {
                addLine();
                play(client, random, 0.8F, SoundEvents.SCULK_SHRIEKER_SHRIEK,
                        SoundEvents.AMBIENT_CAVE.value(), SoundEvents.WARDEN_HEARTBEAT);
            }
            case ENTITY -> {
                glitchTicks = Math.max(glitchTicks, 45);
                addLine();
                play(client, random, 1.2F, SoundEvents.ENDERMAN_SCREAM,
                        SoundEvents.WARDEN_HEARTBEAT, SoundEvents.SCULK_SHRIEKER_SHRIEK);
            }
            case JUMPSCARE -> {
                glitchTicks = 150;
                titleGlitchTicks = 150;
                addLine();
                addLine();
                addLine();
                addLine();
                play(client, random, 1.6F, SoundEvents.WARDEN_ROAR,
                        SoundEvents.ENDERMAN_SCREAM, SoundEvents.WITHER_SPAWN);
            }
            case TITLE_GLITCH -> {
                titleGlitchTicks = Math.max(titleGlitchTicks, 90);
                addLine();
            }
            case FINALE -> {
                glitchTicks = 220;
                titleGlitchTicks = 220;
                addLine();
                addLine();
                addLine();
                play(client, random, 1.4F, SoundEvents.WITHER_SPAWN,
                        SoundEvents.WARDEN_ROAR, SoundEvents.END_PORTAL_SPAWN);
            }
            default -> {
            }
        }
    }

    private static void play(Minecraft client, Random random, float volume, SoundEvent... sounds) {
        SoundEvent sound = sounds[random.nextInt(sounds.length)];
        client.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume,
                0.35F + random.nextFloat() * 0.45F));
    }

    private static void addLine() {
        Random random = new Random(visualSeed ^ (session.ticks() * 17L) ^ (lineCounter++ * 0x9E3779B9L));
        LINES.add(0, Error422Text.line(random));
        while (LINES.size() > 14) {
            LINES.remove(LINES.size() - 1);
        }
    }

    public static boolean isActive() {
        return session != null;
    }

    public static boolean visualsEnabled() {
        return visualsEnabled;
    }

    public static Session session() {
        return session;
    }

    public static List<String> lines() {
        return LINES;
    }

    public static int glitchTicks() {
        return glitchTicks;
    }

    public static int titleGlitchTicks() {
        return titleGlitchTicks;
    }

    public static long visualSeed() {
        return visualSeed;
    }

    public static boolean shouldCorruptHud() {
        Error422Settings settings = Error422Config.get();
        if (session == null || !settings.enabled || !settings.corruptHud) {
            return false;
        }
        Phase phase = session.phase();
        return phase == Phase.CORRUPT || phase == Phase.FINAL;
    }
}

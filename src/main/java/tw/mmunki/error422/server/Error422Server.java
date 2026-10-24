package tw.mmunki.error422.server;

import tw.mmunki.error422.Error422Config;
import tw.mmunki.error422.Error422Settings;
import tw.mmunki.error422.core.Anomaly;
import tw.mmunki.error422.core.Phase;
import tw.mmunki.error422.core.Session;
import tw.mmunki.error422.net.ControlPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Error422Server {
    private final Map<UUID, Session> sessions = new HashMap<>();
    private MinecraftServer server;

    public void start(MinecraftServer server) {
        this.server = server;
        Error422Config.load();
        sessions.clear();
    }

    public void stop(MinecraftServer server) {
        sessions.clear();
        this.server = null;
    }

    public void tick(MinecraftServer server) {
        if (sessions.isEmpty()) {
            return;
        }
        Error422Settings settings = Error422Config.get();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Session session = sessions.get(player.getUUID());
            if (session == null) {
                continue;
            }
            if (!settings.enabled) {
                stopSession(player, true);
                continue;
            }
            session.tick();
            if (session.finished()) {
                stopSession(player, true);
                continue;
            }
            if (settings.disableInCreative && (player.isCreative() || player.isSpectator())) {
                continue;
            }
            for (Anomaly anomaly : session.pollEvents(4)) {
                ServerEffects.apply(player, session, anomaly, settings);
            }
        }
    }

    public void onJoin(ServerPlayer player) {
        Error422Settings settings = Error422Config.get();
        if (!settings.enabled) {
            sendStop(player);
            return;
        }
        if (settings.disableInCreative && (player.isCreative() || player.isSpectator())) {
            sendStop(player);
            return;
        }
        long seed = settings.seed != 0L
                ? settings.seed
                : player.level().getRandom().nextLong();
        seed ^= player.getUUID().getLeastSignificantBits();
        startSession(player, settings.sessionSeconds(), seed);
    }

    public void onDisconnect(ServerPlayer player) {
        sessions.remove(player.getUUID());
    }

    public void startSession(ServerPlayer player, int durationSeconds, long seed) {
        Session session = new Session(seed, Math.max(20, durationSeconds));
        sessions.put(player.getUUID(), session);
        if (ServerPlayNetworking.canSend(player, ControlPayload.TYPE)) {
            ServerPlayNetworking.send(player,
                    new ControlPayload(ControlPayload.Action.START, durationSeconds, seed));
        }
    }

    public void startAll(int durationSeconds) {
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            long seed = player.level().getRandom().nextLong()
                    ^ player.getUUID().getLeastSignificantBits();
            startSession(player, durationSeconds, seed);
        }
    }

    public void stopAll() {
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            stopSession(player, true);
        }
        sessions.clear();
    }

    public void stopSession(ServerPlayer player, boolean notify) {
        sessions.remove(player.getUUID());
        if (notify) {
            sendStop(player);
        }
    }

    public void reload() {
        Error422Config.reload();
    }

    public Session session(ServerPlayer player) {
        return sessions.get(player.getUUID());
    }

    public int activeCount() {
        return sessions.size();
    }

    public String status(ServerPlayer player) {
        Session session = session(player);
        if (session == null) {
            return "ERROR422 is inactive.";
        }
        Phase phase = session.phase();
        return String.format("ERROR422 %s · %.1f%% · seed %d · events %d",
                phase, session.progress() * 100.0D, session.seed(), session.eventIndex());
    }

    private void sendStop(ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, ControlPayload.TYPE)) {
            ServerPlayNetworking.send(player,
                    new ControlPayload(ControlPayload.Action.STOP, 0, 0L));
        }
    }
}

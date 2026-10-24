package dev.mmunki.error422;

import dev.mmunki.error422.command.Error422Commands;
import dev.mmunki.error422.net.ControlPayload;
import dev.mmunki.error422.server.Error422Server;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class Error422Mod implements ModInitializer {
    private final Error422Server server = new Error422Server();

    @Override
    public void onInitialize() {
        Error422Config.load();
        ControlPayload.register();

        ServerLifecycleEvents.SERVER_STARTING.register(server::start);
        ServerLifecycleEvents.SERVER_STOPPING.register(server::stop);
        ServerTickEvents.END_SERVER_TICK.register(server::tick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, minecraftServer) ->
                server.onJoin(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, minecraftServer) ->
                server.onDisconnect(handler.player));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                Error422Commands.register(dispatcher, server));

        Error422.LOGGER.info("ERROR422 {} loaded for Minecraft 26.2 (client + server, safe mode by default)",
                Error422.VERSION);
    }
}

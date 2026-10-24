package tw.mmunki.error422.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import tw.mmunki.error422.Error422Config;
import tw.mmunki.error422.server.Error422Server;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public final class Error422Commands {
    private Error422Commands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Error422Server server) {
        dispatcher.register(Commands.literal("error422")
                .executes(context -> status(context.getSource(), server))
                .then(Commands.literal("help")
                        .executes(context -> help(context.getSource())))
                .then(Commands.literal("status")
                        .executes(context -> status(context.getSource(), server)))
                .then(Commands.literal("start")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> start(context.getSource(), server,
                                Error422Config.get().sessionSeconds()))
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 240))
                                .executes(context -> start(context.getSource(), server,
                                        IntegerArgumentType.getInteger(context, "minutes") * 60))))
                .then(Commands.literal("demo")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> start(context.getSource(), server, 180)))
                .then(Commands.literal("stop")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> stop(context.getSource(), server)))
                .then(Commands.literal("reset")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> reset(context.getSource(), server)))
                .then(Commands.literal("reload")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> reload(context.getSource(), server))));
    }

    private static int start(CommandSourceStack source, Error422Server server, int seconds) {
        ServerPlayer player = source.getPlayer();
        if (player != null) {
            long seed = player.level().getRandom().nextLong() ^ player.getUUID().getLeastSignificantBits();
            server.startSession(player, seconds, seed);
            source.sendSuccess(() -> Component.literal(
                    "ERROR422 started for " + player.getScoreboardName() + " for " + (seconds / 60) + " minutes.")
                    .withStyle(ChatFormatting.RED), false);
        } else {
            server.startAll(seconds);
            source.sendSuccess(() -> Component.literal(
                    "ERROR422 started for " + server.activeCount() + " players for " + (seconds / 60) + " minutes.")
                    .withStyle(ChatFormatting.RED), true);
        }
        return 1;
    }

    private static int stop(CommandSourceStack source, Error422Server server) {
        ServerPlayer player = source.getPlayer();
        if (player != null) {
            server.stopSession(player, true);
            source.sendSuccess(() -> Component.literal("ERROR422 stopped for you.")
                    .withStyle(ChatFormatting.GRAY), false);
        } else {
            int count = server.activeCount();
            server.stopAll();
            source.sendSuccess(() -> Component.literal("ERROR422 stopped for " + count + " players.")
                    .withStyle(ChatFormatting.GRAY), true);
        }
        return 1;
    }

    private static int status(CommandSourceStack source, Error422Server server) {
        ServerPlayer player = source.getPlayer();
        if (player != null) {
            source.sendSuccess(() -> Component.literal(server.status(player))
                    .withStyle(ChatFormatting.RED), false);
        } else {
            source.sendSuccess(() -> Component.literal(
                    "ERROR422 active sessions: " + server.activeCount())
                    .withStyle(ChatFormatting.RED), false);
        }
        return 1;
    }

    private static int reset(CommandSourceStack source, Error422Server server) {
        server.stopAll();
        server.reload();
        source.sendSuccess(() -> Component.literal("ERROR422 reset. Config reloaded.")
                .withStyle(ChatFormatting.GRAY), true);
        return 1;
    }

    private static int reload(CommandSourceStack source, Error422Server server) {
        server.reload();
        source.sendSuccess(() -> Component.literal("ERROR422 config reloaded: enabled="
                        + Error422Config.get().enabled
                        + " minutes=" + Error422Config.get().sessionMinutes
                        + " intensity=" + Error422Config.get().intensity)
                .withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    private static int help(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("ERROR422 commands:").withStyle(ChatFormatting.RED), false);
        source.sendSuccess(() -> Component.literal("/error422 status — show your session").withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("/error422 start [minutes] — start a session").withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("/error422 demo — start a 3 minute demo").withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("/error422 stop — stop your session").withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("/error422 reset — stop all and reload config").withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("/error422 reload — reload config/error422.json").withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("F8 toggles the client visual overlay.").withStyle(ChatFormatting.DARK_GRAY), false);
        return 1;
    }
}

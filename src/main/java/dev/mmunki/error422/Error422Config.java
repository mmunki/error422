package dev.mmunki.error422;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Error422Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("error422.json");
    private static Error422Settings settings = new Error422Settings();

    private Error422Config() {
    }

    public static Error422Settings get() {
        return settings;
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            Error422Settings loaded = GSON.fromJson(reader, Error422Settings.class);
            if (loaded != null) {
                settings = loaded.normalized();
            }
        } catch (IOException | RuntimeException exception) {
            Error422.LOGGER.warn("Could not read {}", FILE, exception);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(settings, writer);
            }
        } catch (IOException exception) {
            Error422.LOGGER.warn("Could not write {}", FILE, exception);
        }
    }

    public static void reload() {
        load();
    }
}

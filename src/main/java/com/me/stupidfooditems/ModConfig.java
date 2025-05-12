package com.me.stupidfooditems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ModConfig {
    private static final Path CONFIG_PATH = Path.of("config", Stupidfooditems.MOD_ID + ".properties");
    private static final Properties properties = new Properties();

    // Default values
    public static boolean PARTICLE_EFFECTS = true;
    public static boolean SERVER_SPAWNING = true;
    public static int NUKE_TIME = 1200; // 20 ticks * 60 seconds

    static {
        loadConfig();
    }

    private static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                properties.load(Files.newInputStream(CONFIG_PATH));
                PARTICLE_EFFECTS = Boolean.parseBoolean(properties.getProperty("particle_effects", "true"));
                SERVER_SPAWNING = Boolean.parseBoolean(properties.getProperty("server_spawning", "true"));
                NUKE_TIME = Integer.parseInt(properties.getProperty("nuke_time", "1200"));
            } catch (IOException e) {
                System.err.println("Failed to load config, using defaults");
                setDefaults();
            }
        } else {
            setDefaults();
            saveConfig();
        }
    }

    private static void setDefaults() {
        properties.setProperty("particle_effects", "true");
        properties.setProperty("server_spawning", "true");
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            properties.store(Files.newOutputStream(CONFIG_PATH), "Stupid Food Items Config");
        } catch (IOException e) {
            System.err.println("Failed to save config");
        }
    }

    public static void setParticleEffects(boolean enabled) {
        PARTICLE_EFFECTS = enabled;
        properties.setProperty("particle_effects", String.valueOf(enabled));
        saveConfig();
    }

    public static void setServerSpawning(boolean enabled) {
        SERVER_SPAWNING = enabled;
        properties.setProperty("server_spawning", String.valueOf(enabled));
        saveConfig();
    }

    public static void setNukeTime(int nukeTime) {
        NUKE_TIME = nukeTime;
        properties.setProperty("server_spawning", String.valueOf(nukeTime));
        saveConfig();
    }
}
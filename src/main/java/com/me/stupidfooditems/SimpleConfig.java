package com.me.stupidfooditems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SimpleConfig {
    private static final Path CONFIG_PATH = Path.of("config", Stupidfooditems.MOD_ID + ".properties");
    private static final Properties properties = new Properties();

    // Configuration values
    public static boolean PARTICLE_EFFECTS_ENABLED = true;
    public static boolean SPAWN_ENTITIES_ENABLED = true;
    public static int NUKE_TIME = 1200; // 20 ticks * 60 seconds

    // Initialize the config - call this in your mod initialization
    public static void initialize() {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                properties.load(Files.newInputStream(CONFIG_PATH));
                PARTICLE_EFFECTS_ENABLED = Boolean.parseBoolean(
                        properties.getProperty("particle_effects", "true"));
                SPAWN_ENTITIES_ENABLED = Boolean.parseBoolean(
                        properties.getProperty("spawn_entities", "true"));
                NUKE_TIME = Integer.parseInt(properties.getProperty("nuke_time", "1200"));
            } else {
                // Create default config
                properties.setProperty("particle_effects", "true");
                properties.setProperty("spawn_entities", "true");
                properties.setProperty("nuke_time", "1200");
                saveConfig();
            }
        } catch (IOException e) {
            System.err.println("Failed to load config, using defaults");
        }
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            properties.store(Files.newOutputStream(CONFIG_PATH), "Stupid Food Items Config");
        } catch (IOException e) {
            System.err.println("Failed to save config");
        }
    }
}
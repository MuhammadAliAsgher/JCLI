package com.mh.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private Map<String, String> aliases = new HashMap<>();
    private String prompt = "%s";
    private static final Path CONFIG_FILE = Paths.get(System.getProperty("user.home"), ".clirc");

    public Config() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                Files.readAllLines(CONFIG_FILE).forEach(line -> {
                    if (line.startsWith("alias ")) {
                        String[] parts = line.substring(6).split("=", 2);
                        if (parts.length == 2) {
                            aliases.put(parts[0].trim(), parts[1].trim().replaceAll("^'|'$", ""));
                        }
                    } else if (line.startsWith("prompt=")) {
                        prompt = line.substring(7).trim();
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    public void setAlias(String name, String value) {
        aliases.put(name, value);
        saveConfig();
    }

    public String getPrompt(String currentDir) {
        return String.format(prompt, currentDir);
    }

    private void saveConfig() {
        try {
            StringBuilder config = new StringBuilder();
            config.append("prompt=").append(prompt).append("\n");
            aliases.forEach((k, v) -> config.append("alias ").append(k).append("=").append("'").append(v).append("'").append("\n"));
            Files.write(CONFIG_FILE, config.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
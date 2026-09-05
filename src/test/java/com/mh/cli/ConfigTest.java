package com.mh.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {
    private Path tempDir;
    private Path configFile;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("clirc-test");
        configFile = tempDir.resolve(".clirc");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
        });
    }

    @Test
    void missingConfigFileYieldsDefaultsWithoutCrashing() {
        Config config = new Config(configFile);

        assertTrue(config.getAliases().isEmpty());
        assertEquals("mydir", config.getPrompt("mydir"));
    }

    @Test
    void setAliasPersistsAndIsVisibleViaGetAliases() {
        Config config = new Config(configFile);

        config.setAlias("ll", "ls -la");

        assertEquals("ls -la", config.getAliases().get("ll"));
        assertTrue(Files.exists(configFile));
    }

    @Test
    void loadsAliasFromExistingConfigFile() throws IOException {
        Files.writeString(configFile, "alias ll='ls -la'\n");

        Config config = new Config(configFile);

        assertEquals("ls -la", config.getAliases().get("ll"));
    }

    @Test
    void loadsPromptFromExistingConfigFile() throws IOException {
        Files.writeString(configFile, "prompt=$ %s>\n");

        Config config = new Config(configFile);

        assertEquals("$ /tmp>", config.getPrompt("/tmp"));
    }

    @Test
    void malformedAliasLineIsSkippedNotCrashed() throws IOException {
        Files.writeString(configFile, "alias noequalssign\n");

        Config config = new Config(configFile);

        assertTrue(config.getAliases().isEmpty());
    }

    @Test
    void reloadingAfterSetAliasPicksUpPersistedValue() {
        Config first = new Config(configFile);
        first.setAlias("ll", "ls -la");

        Config second = new Config(configFile);

        assertEquals("ls -la", second.getAliases().get("ll"));
    }
}

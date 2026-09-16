package com.mh.cli.commands;

import com.mh.cli.Config;
import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AliasCommandTest {
    private AliasCommand cmd;
    private Shell shell;
    private Config config;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;
    private Path configFile;

    private Path tempDir;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("alias-test");
        configFile = tempDir.resolve(".clirc");
        config = new Config(configFile);
        cmd = new AliasCommand(config);
        shell = Shell.forTesting();
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testSetAlias() throws IOException {
        cmd.execute(List.of("ll=\"ls -l\""), "", shell);
        assertEquals("\"ls -l\"", config.getAliases().get("ll"));
        assertTrue(Files.exists(configFile));
        assertTrue(Files.readString(configFile).contains("ll='\"ls -l\"'"));
    }

    @Test
    void testListAliases() throws IOException {
        config.setAlias("ll", "ls -l");
        cmd.execute(List.of(), "", shell);
        assertEquals("ll='ls -l'\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testInvalidAliasFormat() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid"), "", shell));
    }

    @Test
    void testEmptyAliases() throws IOException {
        cmd.execute(List.of(), "", shell);
        assertEquals("", out.toString());
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() throws IOException {
        System.setOut(originalOut);
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
        });
    }
}
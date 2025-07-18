package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CdCommandTest {
    private CdCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new CdCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testCdToDir() throws IOException {
        Files.createDirectory(tempDir.resolve("subdir"));
        cmd.execute(List.of("subdir"), "", shell);
        assertEquals(tempDir.resolve("subdir").toString(), shell.getCurrentDir());
    }

    @Test
    void testCdNoArgs() throws IOException {
        // CdCommand tries to go to user.home, but Shell's security prevents going outside working directory
        // So we expect an IOException for this security violation
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testCdInvalidDir() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid"), "", shell));
    }

    @Test
    void testCdPathTraversal() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("../outside"), "", shell));
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
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

class RmCommandTest {
    private RmCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new RmCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("file.txt"), "content".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testRmFile() throws IOException {
        cmd.execute(List.of("file.txt"), "", shell);
        assertFalse(Files.exists(tempDir.resolve("file.txt")));
    }

    @Test
    void testRmDirectory() throws IOException {
        Files.createDirectory(tempDir.resolve("dir"));
        assertThrows(IOException.class, () -> cmd.execute(List.of("dir"), "", shell));
    }

    @Test
    void testRmNonExistentFile() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid.txt"), "", shell));
    }

    @Test
    void testRmNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testRmPathTraversal() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("../outside.txt"), "", shell));
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
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

class MvCommandTest {
    private MvCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new MvCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("src.txt"), "content".getBytes());
        Files.createDirectory(tempDir.resolve("dest"));
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testMvFile() throws IOException {
        cmd.execute(List.of("src.txt", "dest.txt"), "", shell);
        assertFalse(Files.exists(tempDir.resolve("src.txt")));
        assertTrue(Files.exists(tempDir.resolve("dest.txt")));
        assertEquals("content", Files.readString(tempDir.resolve("dest.txt")));
    }

    @Test
    void testMvToDirectory() throws IOException {
        // MvCommand like CpCommand doesn't automatically move into directory
        // It treats destination as a file path, so we need to specify the full path
        cmd.execute(List.of("src.txt", "dest/src.txt"), "", shell);
        assertFalse(Files.exists(tempDir.resolve("src.txt")));
        assertTrue(Files.exists(tempDir.resolve("dest/src.txt")));
        assertEquals("content", Files.readString(tempDir.resolve("dest/src.txt")));
    }

    @Test
    void testMvInvalidSource() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid", "dest.txt"), "", shell));
    }

    @Test
    void testMvTooFewArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("src.txt"), "", shell));
    }

    @Test
    void testMvPathTraversal() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("src.txt", "../outside.txt"), "", shell));
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
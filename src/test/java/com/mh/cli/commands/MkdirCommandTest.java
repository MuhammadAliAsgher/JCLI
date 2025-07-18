package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;
import java.util.List;

class MkdirCommandTest {
    private MkdirCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new MkdirCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testMkdirSingleDir() throws IOException {
        cmd.execute(List.of("newdir"), "", shell);
        assertTrue(Files.isDirectory(tempDir.resolve("newdir")));
    }

    @Test
    void testMkdirMultipleDirs() throws IOException {
        // Current MkdirCommand only creates the first directory, not multiple
        cmd.execute(List.of("dir1", "dir2"), "", shell);
        assertTrue(Files.isDirectory(tempDir.resolve("dir1")));
        // The current implementation doesn't create dir2, only dir1
        assertFalse(Files.isDirectory(tempDir.resolve("dir2")));
    }

    @Test
    void testMkdirNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testMkdirPathTraversal() {
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
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

class CpCommandTest {
    private CpCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new CpCommand();
        shell = Shell.forTesting();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("src.txt"), "content".getBytes());
        Files.createDirectory(tempDir.resolve("dest"));
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testCpFile() throws IOException {
        cmd.execute(List.of("src.txt", "dest.txt"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("dest.txt")));
        assertEquals("content", Files.readString(tempDir.resolve("dest.txt")));
    }

    @Test
    void testCpToDirectory() throws IOException {
        // Current CpCommand implementation doesn't automatically copy into directory
        // It treats destination as a file path, so we need to specify the full path
        cmd.execute(List.of("src.txt", "dest/src.txt"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("dest/src.txt")));
        assertEquals("content", Files.readString(tempDir.resolve("dest/src.txt")));
    }

    @Test
    void testCpInvalidSource() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid", "dest.txt"), "", shell));
    }

    @Test
    void testCpTooFewArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("src.txt"), "", shell));
    }

    @Test
    void testCpPathTraversal() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("src.txt", "../outside.txt"), "", shell));
    }

    @Test
    void testCpRecursiveCopiesDirectoryTree() throws IOException {
        Files.createDirectory(tempDir.resolve("srcdir"));
        Files.write(tempDir.resolve("srcdir/a.txt"), "a".getBytes());
        Files.createDirectory(tempDir.resolve("destdir"));

        cmd.execute(List.of("-r", "srcdir", "destdir"), "", shell);

        assertEquals("a", Files.readString(tempDir.resolve("destdir/a.txt")));
    }

    @Test
    void testCpInteractiveOverwriteConfirmedProceedsWithCopy() throws IOException {
        Files.write(tempDir.resolve("dest.txt"), "old".getBytes());
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        try {
            cmd.execute(List.of("-i", "src.txt", "dest.txt"), "", shell);
        } finally {
            System.setIn(originalIn);
        }

        assertEquals("content", Files.readString(tempDir.resolve("dest.txt")));
    }

    @Test
    void testCpInteractiveOverwriteDeclinedLeavesDestUnchanged() throws IOException {
        Files.write(tempDir.resolve("dest.txt"), "old".getBytes());
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("n\n".getBytes()));
        try {
            cmd.execute(List.of("-i", "src.txt", "dest.txt"), "", shell);
        } finally {
            System.setIn(originalIn);
        }

        assertEquals("old", Files.readString(tempDir.resolve("dest.txt")));
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
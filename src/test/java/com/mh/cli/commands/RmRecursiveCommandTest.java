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

class RmRecursiveCommandTest {
    private RmRecursiveCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;
    private ByteArrayInputStream in;
    private InputStream originalIn;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new RmRecursiveCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.createDirectory(tempDir.resolve("dir"));
        Files.write(tempDir.resolve("dir/file.txt"), "content".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testRmRecursiveWithConfirmation() throws IOException {
        in = new ByteArrayInputStream("y\n".getBytes());
        originalIn = System.in;
        System.setIn(in);
        cmd.execute(List.of("dir"), "", shell);
        assertFalse(Files.exists(tempDir.resolve("dir")));
    }

    @Test
    void testRmRecursiveNoConfirmation() throws IOException {
        in = new ByteArrayInputStream("n\n".getBytes());
        originalIn = System.in;
        System.setIn(in);
        cmd.execute(List.of("dir"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("dir")));
    }

    @Test
    void testRmRecursiveNonExistentPath() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid"), "", shell));
    }

    @Test
    void testRmRecursiveNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testRmRecursivePathTraversal() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("../outside"), "", shell));
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
        });
    }
}
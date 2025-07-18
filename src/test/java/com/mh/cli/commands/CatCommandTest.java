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

class CatCommandTest {
    private CatCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new CatCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("file.txt"), "line1\nline2".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testCatFile() throws IOException {
        cmd.execute(List.of("file.txt"), "", shell);
        assertEquals("line1\nline2\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testCatPipedInput() throws IOException {
        cmd.execute(List.of(), "input\nline2", shell);
        assertEquals("input\nline2\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testCatNonExistentFile() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid.txt"), "", shell));
    }

    @Test
    void testCatNoArgsNoInput() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testCatPathTraversal() {
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
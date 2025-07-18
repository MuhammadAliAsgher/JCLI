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

class TailCommandTest {
    private TailCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new TailCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("file.txt"), "line1\nline2\nline3\nline4".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testTailDefault() throws IOException {
        cmd.execute(List.of("file.txt"), "", shell);
        assertEquals("line1\nline2\nline3\nline4\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testTailWithN() throws IOException {
        cmd.execute(List.of("-n", "2", "file.txt"), "", shell);
        assertEquals("line3\nline4\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testTailPipedInput() throws IOException {
        cmd.execute(List.of(), "line1\nline2\nline3", shell);
        assertEquals("line1\nline2\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testTailInvalidN() {
        assertThrows(NumberFormatException.class, () -> cmd.execute(List.of("-n", "invalid", "file.txt"), "", shell));
    }

    @Test
    void testTailNonExistentFile() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid.txt"), "", shell));
    }

    @Test
    void testTailPathTraversal() {
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
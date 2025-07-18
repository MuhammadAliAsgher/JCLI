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

class SortCommandTest {
    private SortCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new SortCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("file.txt"), "line2\nline1\nline3".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testSortFile() throws IOException {
        cmd.execute(List.of("file.txt"), "", shell);
        assertEquals("line1\nline2\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testSortReverse() throws IOException {
        cmd.execute(List.of("-r", "file.txt"), "", shell);
        assertEquals("line3\nline2\nline1\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testSortPipedInput() throws IOException {
        cmd.execute(List.of(), "line2\nline1\nline3", shell);
        assertEquals("line1\nline2\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testSortEmptyFile() throws IOException {
        Files.write(tempDir.resolve("empty.txt"), "".getBytes());
        cmd.execute(List.of("empty.txt"), "", shell);
        assertEquals("", out.toString());
    }

    @Test
    void testSortNonExistentFile() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid.txt"), "", shell));
    }

    @Test
    void testSortPathTraversal() {
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
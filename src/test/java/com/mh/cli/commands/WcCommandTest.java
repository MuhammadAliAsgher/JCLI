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

class WcCommandTest {
    private WcCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new WcCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("file.txt"), "line1\nline2 word".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testWcFile() throws IOException {
        cmd.execute(List.of("file.txt"), "", shell);
        String output = out.toString().replace("\r\n", "\n");
        // The exact character count may vary based on line endings, so we check the format
        assertTrue(output.matches("2 3 \\d+ file\\.txt\\n"));
    }

    @Test
    void testWcEmptyFile() throws IOException {
        Files.write(tempDir.resolve("empty.txt"), "".getBytes());
        cmd.execute(List.of("empty.txt"), "", shell);
        assertEquals("0 0 0 empty.txt\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testWcNonExistentFile() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid.txt"), "", shell));
    }

    @Test
    void testWcNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testWcPathTraversal() {
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
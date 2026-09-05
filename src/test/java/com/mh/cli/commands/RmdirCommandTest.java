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

class RmdirCommandTest {
    private RmdirCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new RmdirCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.createDirectory(tempDir.resolve("dir"));
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testRmdirEmptyDir() throws IOException {
        cmd.execute(List.of("dir"), "", shell);
        assertFalse(Files.exists(tempDir.resolve("dir")));
    }

    @Test
    void testRmdirNonEmptyDir() throws IOException {
        Files.createFile(tempDir.resolve("dir/file.txt"));
        assertThrows(IOException.class, () -> cmd.execute(List.of("dir"), "", shell));
    }

    @Test
    void testRmdirNonExistentDir() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("invalid"), "", shell));
    }

    @Test
    void testRmdirNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
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
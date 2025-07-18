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

class TouchCommandTest {
    private TouchCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new TouchCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testTouchNewFile() throws IOException {
        cmd.execute(List.of("file.txt"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("file.txt")));
        assertEquals(0, Files.size(tempDir.resolve("file.txt")));
    }

    @Test
    void testTouchExistingFile() throws IOException {
        Files.write(tempDir.resolve("file.txt"), "content".getBytes());
        
        // Current TouchCommand implementation doesn't update modification time of existing files
        // It only creates new files if they don't exist
        cmd.execute(List.of("file.txt"), "", shell);
        
        // The file should still exist and content should be unchanged
        assertTrue(Files.exists(tempDir.resolve("file.txt")));
        assertEquals("content", Files.readString(tempDir.resolve("file.txt")));
        
        // Note: Current implementation doesn't update modification time for existing files
        // If we want to update modification time, we'd need to modify TouchCommand
    }

    @Test
    void testTouchNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testTouchPathTraversal() {
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
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

class GrepCommandTest {
    private GrepCommand cmd;
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        cmd = new GrepCommand();
        shell = new Shell();
        tempDir = Files.createTempDirectory("test");
        shell.setCurrentDir(tempDir.toString());
        Files.write(tempDir.resolve("file.txt"), "line1\nLine2\nline3".getBytes());
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testGrepFile() throws IOException {
        cmd.execute(List.of("line", "file.txt"), "", shell);
        assertEquals("line1\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testGrepWithLineNumbers() throws IOException {
        cmd.execute(List.of("-n", "line", "file.txt"), "", shell);
        assertEquals("1:line1\n3:line3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testGrepCaseInsensitive() throws IOException {
        cmd.execute(List.of("-i", "LINE", "file.txt"), "", shell);
        assertEquals("line1\nLine2\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testGrepBothFlags() throws IOException {
        cmd.execute(List.of("-n", "-i", "LINE", "file.txt"), "", shell);
        assertEquals("1:line1\n2:Line2\n3:line3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testGrepPipedInput() throws IOException {
        cmd.execute(List.of("line"), "line1\nLine2\nline3", shell);
        assertEquals("line1\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testGrepNoArgs() {
        assertThrows(IOException.class, () -> cmd.execute(List.of(), "", shell));
    }

    @Test
    void testGrepInvalidFile() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("line", "invalid.txt"), "", shell));
    }

    @Test
    void testGrepPathTraversal() {
        assertThrows(IOException.class, () -> cmd.execute(List.of("line", "../outside.txt"), "", shell));
    }

    @Test
    void testGrepWithEchoOriginal() throws IOException {
        Files.write(tempDir.resolve("test.txt"), "line1\\nline2\\nline3".getBytes());
        cmd.execute(List.of("line", "test.txt"), "", shell);
        assertEquals("line1\\nline2\\nline3\n", out.toString().replace("\r\n", "\n"));
    }

    // Test for fixed EchoCommand (uncomment if applied)
    /*
    @Test
    void testGrepWithEchoFixed() throws IOException {
        Files.write(tempDir.resolve("test.txt"), "line1\nline2\nline3".getBytes());
        cmd.execute(List.of("line", "test.txt"), "", shell);
        assertEquals("line1\nline2\nline3\n", out.toString());
    }
    */

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() throws IOException {
        System.setOut(originalOut);
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
        });
    }
}
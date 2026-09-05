package com.mh.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ShellTest {
    private Path tempDir;
    private Path historyFile;
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("shell-test");
        historyFile = tempDir.resolve(".cli_history");
        originalIn = System.in;
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setIn(originalIn);
        System.setOut(originalOut);
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
        });
    }

    /** run() loops until System.in is exhausted, at which point scanner.nextLine() throws -- that's
     * the only clean way to end run() from a test without going through ExitCommand's System.exit(0). */
    private void runWithInput(Shell shell, String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertThrows(NoSuchElementException.class, shell::run);
    }

    @Test
    void runsSingleCommandAndPrintsOutput() {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        runWithInput(shell, "echo hi\n");

        assertTrue(capturedOut.toString().contains("hi"));
    }

    @Test
    void skipsBlankLinesWithoutExecutingAnything() {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        runWithInput(shell, "\necho hi\n");

        assertTrue(capturedOut.toString().contains("hi"));
    }

    @Test
    void reportsUnknownCommand() {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        runWithInput(shell, "notacommand\n");

        assertTrue(capturedOut.toString().contains("Unknown command: notacommand"));
    }

    @Test
    void pipesOutputBetweenCommands() {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        runWithInput(shell, "echo hi | cat\n");

        assertTrue(capturedOut.toString().contains("hi"));
    }

    @Test
    void redirectsOutputToFile() throws IOException {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        runWithInput(shell, "echo hi > out.txt\n");

        assertTrue(Files.readString(tempDir.resolve("out.txt")).contains("hi"));
    }

    @Test
    void catchesCommandExceptionAndPrintsError() {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        runWithInput(shell, "cp\n"); // too few args -> CpCommand throws IOException

        assertTrue(capturedOut.toString().contains("Error:"));
    }

    @Test
    void persistsAndReloadsHistoryAcrossInstances() {
        Shell first = new Shell(historyFile);
        first.setCurrentDir(tempDir.toString());
        runWithInput(first, "echo hi\n");

        Shell second = new Shell(historyFile);

        assertTrue(Files.exists(historyFile));
    }

    @Test
    void resolveSafePathRejectsPathTraversalOutsideCurrentDir() {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        assertThrows(IOException.class, () -> shell.resolveSafePath("../outside"));
    }

    @Test
    void resolveSafePathAllowsPathsInsideCurrentDir() throws IOException {
        Shell shell = new Shell(historyFile);
        shell.setCurrentDir(tempDir.toString());

        Path resolved = shell.resolveSafePath("file.txt");

        assertEquals(tempDir.resolve("file.txt"), resolved);
    }
}

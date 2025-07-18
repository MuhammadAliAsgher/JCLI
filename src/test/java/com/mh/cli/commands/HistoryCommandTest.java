package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HistoryCommandTest {
    private HistoryCommand cmd;
    private Shell shell;
    private List<String> history;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        history = new ArrayList<>(Arrays.asList("cmd1", "cmd2", "cmd3"));
        cmd = new HistoryCommand(history);
        shell = new Shell();
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testHistoryAll() throws IOException {
        cmd.execute(List.of(), "", shell);
        assertEquals("1: cmd1\n2: cmd2\n3: cmd3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testHistoryWithN() throws IOException {
        cmd.execute(List.of("-n", "2"), "", shell);
        assertEquals("2: cmd2\n3: cmd3\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testHistoryClear() throws IOException {
        cmd.execute(List.of("-c"), "", shell);
        assertTrue(history.isEmpty());
        assertEquals("", out.toString());
    }

    @Test
    void testHistoryInvalidN() {
        assertThrows(NumberFormatException.class, () -> cmd.execute(List.of("-n", "invalid"), "", shell));
    }

    @Test
    void testHistoryEmpty() throws IOException {
        history.clear();
        cmd.execute(List.of(), "", shell);
        assertEquals("", out.toString());
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        System.setOut(originalOut);
    }
}
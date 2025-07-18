package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EchoCommandTest {
    private EchoCommand cmd;
    private Shell shell;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        cmd = new EchoCommand();
        shell = new Shell();
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testEchoSingleArg() throws IOException {
        cmd.execute(List.of("hello"), "", shell);
        assertEquals("hello\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testEchoMultipleArgs() throws IOException {
        cmd.execute(List.of("hello", "world"), "", shell);
        assertEquals("hello world\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testEchoEmptyArgs() throws IOException {
        cmd.execute(List.of(), "", shell);
        assertEquals("\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testEchoWithNewlinesOriginal() throws IOException {
        cmd.execute(List.of("line1\\nline2"), "", shell);
        assertEquals("line1\\nline2\n", out.toString().replace("\r\n", "\n"));
    }

    // Test for fixed EchoCommand (uncomment if applied)
    /*
    @Test
    void testEchoWithNewlinesFixed() throws IOException {
        cmd.execute(List.of("line1\\nline2"), "", shell);
        assertEquals("line1\nline2\n", out.toString());
    }
    */

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        System.setOut(originalOut);
    }
}
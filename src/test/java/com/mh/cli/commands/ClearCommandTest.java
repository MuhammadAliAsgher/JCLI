package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class ClearCommandTest {
    private ClearCommand cmd;
    private Shell shell;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        cmd = new ClearCommand();
        shell = new Shell();
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testClearUnix() throws IOException {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            cmd.execute(List.of(), "", shell);
            assertEquals("\033[H\033[2J", out.toString());
        }
    }

    @Test
    void testClearWindows() throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // ClearCommand uses ProcessBuilder with inheritIO(), so no output is captured
            // We test that the command executes without throwing an exception
            assertDoesNotThrow(() -> cmd.execute(List.of(), "", shell));
            assertEquals("", out.toString());
        }
    }

    @Test
    void testClearWithArgs() throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // ClearCommand should work regardless of arguments
            assertDoesNotThrow(() -> cmd.execute(List.of("arg"), "", shell));
            assertEquals("", out.toString());
        } else {
            cmd.execute(List.of("arg"), "", shell);
            assertEquals("\033[H\033[2J", out.toString());
        }
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        System.setOut(originalOut);
    }
}
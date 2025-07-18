package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {
    private HelpCommand cmd;
    private Shell shell;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        shell = new Shell();
        Map<String, Command> commands = new HashMap<>();
        commands.put("test", new TestCommand());
        cmd = new HelpCommand(commands);
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testHelpWithoutArgs() throws IOException {
        cmd.execute(List.of(), "", shell);
        String output = out.toString();
        assertTrue(output.contains("Available commands:"));
        assertTrue(output.contains("test"));
    }

    @Test
    void testHelpWithValidCommand() throws IOException {
        cmd.execute(List.of("test"), "", shell);
        String output = out.toString();
        assertTrue(output.contains("Command: test"));
        assertTrue(output.contains("No help available"));
    }

    @Test
    void testHelpWithInvalidCommand() {
        IOException exception = assertThrows(IOException.class, () -> cmd.execute(List.of("invalid"), "", shell));
        assertEquals("Unknown command: invalid", exception.getMessage());
    }

    @Test
    void testHelpWithEmptyCommandList() throws IOException {
        cmd = new HelpCommand(new HashMap<>());
        cmd.execute(List.of(), "", shell);
        String output = out.toString();
        assertTrue(output.contains("Available commands:"));
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        System.setOut(originalOut);
    }

    private static class TestCommand implements Command {
        @Override
        public void execute(List<String> args, String input, Shell shell) throws IOException {
            System.out.println("Test command");
        }
    }
}
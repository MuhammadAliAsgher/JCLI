package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateCommandTest {
    private DateCommand cmd;
    private Shell shell;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        cmd = new DateCommand();
        shell = new Shell();
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testDate() throws IOException {
        cmd.execute(List.of(), "", shell);
        String output = out.toString().replace("\r\n", "\n");
        assertTrue(output.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*\\n"));
    }

    @Test
    void testDateWithArgs() throws IOException {
        cmd.execute(List.of("arg"), "", shell);
        String output = out.toString().replace("\r\n", "\n");
        assertTrue(output.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*\\n"));
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        System.setOut(originalOut);
    }
}
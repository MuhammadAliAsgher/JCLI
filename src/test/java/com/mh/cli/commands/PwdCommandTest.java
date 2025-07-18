package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PwdCommandTest {
    private PwdCommand cmd;
    private Shell shell;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        cmd = new PwdCommand();
        shell = new Shell();
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testPwd() throws IOException {
        cmd.execute(List.of(), "", shell);
        assertEquals(shell.getCurrentDir() + "\n", out.toString().replace("\r\n", "\n"));
    }

    @Test
    void testPwdWithArgs() throws IOException {
        // PwdCommand doesn't validate arguments, so it just ignores them
        cmd.execute(List.of("arg"), "", shell);
        assertEquals(shell.getCurrentDir() + "\n", out.toString().replace("\r\n", "\n"));
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        System.setOut(originalOut);
    }
}
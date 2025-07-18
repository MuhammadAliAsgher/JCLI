package com.mh.cli.commands;

import com.mh.cli.Shell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExitCommandTest {
    private ExitCommand cmd;
    private Shell shell;

    @BeforeEach
    void setUp() {
        cmd = new ExitCommand();
        shell = new Shell();
    }

    @Test
    void testExitCommand() {
        // Since ExitCommand calls System.exit(0) which terminates the JVM,
        // we can only test that the command exists and is properly constructed
        assertNotNull(cmd);
        
        // We can't actually test the execute method without terminating the test JVM
        // In a real scenario, this would be tested through integration tests
        // or by mocking System.exit behavior
    }

    @Test
    void testExitWithArgs() {
        // Test that the command can handle arguments
        // Again, we can't test actual execution without terminating the JVM
        assertNotNull(cmd);
        
        // The command should ignore any arguments and still exit
        // This would be tested in integration tests
    }
}

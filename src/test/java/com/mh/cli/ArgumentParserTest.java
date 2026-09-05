package com.mh.cli;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTest {

    private static final Set<String> COMMANDS = Set.of("ls", "cat", "grep", "echo", "rm", "rm -r");
    private static final Map<String, String> NO_ALIASES = Map.of();

    @Test
    void parsesSingleCommandWithArgs() {
        var pipeline = ArgumentParser.parse("ls -la", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("ls", pipeline.get(0).command);
        assertEquals(List.of("-la"), pipeline.get(0).args);
        assertNull(pipeline.get(0).redirectFile);
    }

    @Test
    void parsesMultiWordRegisteredCommand() {
        // "rm -r" is registered as a two-word command, but parseCommand always re-splits
        // the resolved command string on whitespace -- so "-r" ends up prepended to args,
        // the same way it would for an alias. This is verified actual behavior, not assumed.
        var pipeline = ArgumentParser.parse("rm -r myfile", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("rm", pipeline.get(0).command);
        assertEquals(List.of("-r", "myfile"), pipeline.get(0).args);
    }

    @Test
    void parsesPipeline() {
        var pipeline = ArgumentParser.parse("cat file.txt | grep foo", NO_ALIASES, COMMANDS);

        assertEquals(2, pipeline.size());
        assertEquals("cat", pipeline.get(0).command);
        assertEquals(List.of("file.txt"), pipeline.get(0).args);
        assertEquals("grep", pipeline.get(1).command);
        assertEquals(List.of("foo"), pipeline.get(1).args);
    }

    @Test
    void parsesRedirect() {
        var pipeline = ArgumentParser.parse("echo hi > out.txt", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("echo", pipeline.get(0).command);
        assertEquals(List.of("hi"), pipeline.get(0).args);
        assertEquals("out.txt", pipeline.get(0).redirectFile);
    }

    @Test
    void keepsQuotedSpacesTogetherAsOneToken() {
        var pipeline = ArgumentParser.parse("echo \"hello world\"", NO_ALIASES, COMMANDS);

        assertEquals(List.of("hello world"), pipeline.get(0).args);
    }

    @Test
    void resolvesAliasAndPrependsAliasArgsBeforeUserArgs() {
        var aliases = Map.of("ll", "ls -la");

        var pipeline = ArgumentParser.parse("ll extra", aliases, COMMANDS);

        assertEquals("ls", pipeline.get(0).command);
        assertEquals(List.of("-la", "extra"), pipeline.get(0).args);
    }

    @Test
    void emptyInputProducesEmptyPipeline() {
        var pipeline = ArgumentParser.parse("", NO_ALIASES, COMMANDS);

        assertTrue(pipeline.isEmpty());
    }

    @Test
    void leadingPipeWithNoPrecedingCommandIsSkippedNotCrashed() {
        var pipeline = ArgumentParser.parse("| ls", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("ls", pipeline.get(0).command);
    }

    @Test
    void redirectWithNoPrecedingCommandIsSkippedNotCrashed() {
        var pipeline = ArgumentParser.parse("> ls", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("ls", pipeline.get(0).command);
        assertNull(pipeline.get(0).redirectFile);
    }

    @Test
    void unregisteredCommandIsTreatedAsPlainCommandNoCrash() {
        var pipeline = ArgumentParser.parse("notacommand foo bar", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("notacommand", pipeline.get(0).command);
        assertEquals(List.of("foo", "bar"), pipeline.get(0).args);
    }
}

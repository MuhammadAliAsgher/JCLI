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
        // "rm -r" is registered as a two-word command and must stay intact -- a prior bug
        // here re-split it into command="rm", args=["-r", ...], which meant "rm -r" was
        // unreachable through the real shell (dispatch resolves "rm", not "rm -r", and
        // plain RmCommand doesn't understand "-r" as a flag). Verified via the built jar.
        var pipeline = ArgumentParser.parse("rm -r myfile", NO_ALIASES, COMMANDS);

        assertEquals(1, pipeline.size());
        assertEquals("rm -r", pipeline.get(0).command);
        assertEquals(List.of("myfile"), pipeline.get(0).args);
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

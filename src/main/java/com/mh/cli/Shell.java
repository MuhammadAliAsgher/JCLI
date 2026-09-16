package com.mh.cli;

import com.mh.cli.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Shell {
    private static final Logger logger = LoggerFactory.getLogger(Shell.class);
    private String currentDir = System.getProperty("user.dir");
    private List<String> history = new ArrayList<>();
    private Map<String, Command> commands = new HashMap<>();
    private Config config;
    private final Path historyFile;
    private Scanner inputScanner;
    private static final int MAX_HISTORY = 1000;

    public Shell() {
        this(Paths.get(System.getProperty("user.home"), ".cli_history"));
    }

    Shell(Path historyFile) {
        this.historyFile = historyFile;
        config = new Config();
        loadHistory();
        registerCommands();
    }

    Shell(Path historyFile, Config config) {
        this.historyFile = historyFile;
        this.config = config;
        loadHistory();
        registerCommands();
    }

    /** Isolated Shell for tests: history and config both live in a throwaway temp
     * directory instead of the real developer's ~/.cli_history and ~/.clirc. */
    public static Shell forTesting() {
        try {
            Path tempDir = Files.createTempDirectory("jcli-test-shell");
            Config isolatedConfig = new Config(tempDir.resolve(".clirc"));
            return new Shell(tempDir.resolve(".cli_history"), isolatedConfig);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

private void registerCommands() {
    commands.put("help", new HelpCommand(commands));
    commands.put("echo", new EchoCommand());
    commands.put("pwd", new PwdCommand());
    commands.put("cd", new CdCommand());
    commands.put("ls", new LsCommand());
    commands.put("cp", new CpCommand());
    commands.put("mv", new MvCommand());
    commands.put("mkdir", new MkdirCommand());
    commands.put("rmdir", new RmdirCommand());
    commands.put("touch", new TouchCommand());
    commands.put("rm", new RmCommand());
    commands.put("rm -r", new RmRecursiveCommand());
    commands.put("cat", new CatCommand());
    commands.put("grep", new GrepCommand());
    commands.put("wc", new WcCommand());
    commands.put("head", new HeadCommand());
    commands.put("tail", new TailCommand());
    commands.put("sort", new SortCommand());
    commands.put("history", new HistoryCommand(history));
    commands.put("clear", new ClearCommand());
    commands.put("date", new DateCommand());
    commands.put("alias", new AliasCommand(config));
    commands.put("find", new FindCommand());
    commands.put("exit", new ExitCommand());
}

    public void run() {
        System.out.println("Welcome to Java CLI. Type 'help' for commands.");

        while (true) {
            System.out.print(config.getPrompt(currentDir) + "> ");
            String input = readLine().trim();
            if (input.isEmpty()) continue;

            history.add(input);
            saveHistory();

            try {
                List<ArgumentParser.ParsedCommand> pipeline = ArgumentParser.parse(input, config.getAliases(), commands.keySet());
                StringBuilder output = new StringBuilder();
                for (int i = 0; i < pipeline.size(); i++) {
                    ArgumentParser.ParsedCommand cmd = pipeline.get(i);
                    Command command = commands.get(cmd.command.toLowerCase());
                    if (command == null) {
                        System.out.println("Unknown command: " + cmd.command);
                        break;
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos);
                    PrintStream oldOut = System.out;
                    boolean hasDownstreamConsumer = i < pipeline.size() - 1 || cmd.redirectFile != null;
                    if (hasDownstreamConsumer) {
                        System.setOut(ps);
                    }
                    command.execute(cmd.args, output.toString(), this);
                    System.setOut(oldOut);
                    output = new StringBuilder(baos.toString());
                    if (cmd.redirectFile != null) {
                        Files.write(resolveSafePath(cmd.redirectFile), output.toString().getBytes(),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        output = new StringBuilder();
                    }
                }
            } catch (Exception e) {
                logger.error("Error executing '{}': {}", input, e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void loadHistory() {
        try {
            if (Files.exists(historyFile)) {
                history.addAll(Files.readAllLines(historyFile));
            }
        } catch (IOException e) {
            logger.warn("Failed to load history: {}", e.getMessage());
        }
    }

    private void saveHistory() {
        try {
            if (history.size() > MAX_HISTORY) {
                history = history.subList(history.size() - MAX_HISTORY, history.size());
            }
            Files.write(historyFile, history, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.warn("Failed to save history: {}", e.getMessage());
        }
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    /** Single Scanner shared across the whole Shell instance's lifetime, so an
     * interactive command's confirmation prompt (e.g. "Overwrite? (y/n)") never
     * competes with this loop's own Scanner for the same underlying System.in
     * stream -- two independent Scanners buffering the same stream corrupt each
     * other's reads. Lazily created so tests that swap System.in after
     * constructing the Shell still get the swapped stream. */
    public String readLine() {
        if (inputScanner == null) {
            inputScanner = new Scanner(System.in);
        }
        return inputScanner.nextLine();
    }

    public Path resolveSafePath(String path) throws IOException {
        Path resolved = Paths.get(currentDir).resolve(path).normalize();
        if (!resolved.startsWith(Paths.get(currentDir))) {
            throw new IOException("Access denied: Path outside working directory");
        }
        return resolved;
    }

    public static void main(String[] args) {
        new Shell().run();
    }
}
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
    private static final Path HISTORY_FILE = Paths.get(System.getProperty("user.home"), ".cli_history");
    private static final int MAX_HISTORY = 1000;

    public Shell() {
        config = new Config();
        loadHistory();
        registerCommands();
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
    commands.put("exit", new ExitCommand());
}

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Java CLI. Type 'help' for commands.");

        while (true) {
            System.out.print(config.getPrompt(currentDir) + "> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            history.add(input);
            saveHistory();

            try {
                List<ArgumentParser.ParsedCommand> pipeline = ArgumentParser.parse(input, config.getAliases());
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
                    if (i < pipeline.size() - 1) {
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
            if (Files.exists(HISTORY_FILE)) {
                history.addAll(Files.readAllLines(HISTORY_FILE));
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
            Files.write(HISTORY_FILE, history, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
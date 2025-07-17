package com.mh.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Shell {
    private static final Logger logger = LoggerFactory.getLogger(Shell.class);
    private String currentDir = System.getProperty("user.dir");
    private List<String> history = new ArrayList<>();
    private Config config;
    private static final Path HISTORY_FILE = Paths.get(System.getProperty("user.home"), ".cli_history");
    private static final int MAX_HISTORY = 1000;

    public Shell() {
        config = new Config();
        loadHistory();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Java CLI. Type 'exit' to quit.");

        while (true) {
            System.out.print(config.getPrompt(currentDir) + "> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            history.add(input);
            saveHistory();

            if (input.equals("exit")) {
                System.out.println("Exiting CLI...");
                break;
            } else {
                System.out.println("No commands implemented yet.");
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
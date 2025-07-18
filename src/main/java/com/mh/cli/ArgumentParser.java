package com.mh.cli;

import java.util.*;

public class ArgumentParser {
    static class ParsedCommand {
        String command;
        List<String> args;
        String redirectFile;

        ParsedCommand(String command, List<String> args) {
            this.command = command;
            this.args = args;
        }
    }

    public static List<ParsedCommand> parse(String input, Map<String, String> aliases, Set<String> registeredCommands) {
        List<ParsedCommand> pipeline = new ArrayList<>();
        List<String> tokens = tokenize(input);
        List<String> currentArgs = new ArrayList<>();
        String currentCmd = null;
        boolean expectRedirect = false;

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            // Check for multi-word command (e.g., "rm -r")
            String potentialCmd = currentCmd == null ? token : currentCmd + " " + token;
            if (currentCmd == null && i + 1 < tokens.size() && registeredCommands.contains(potentialCmd + " " + tokens.get(i + 1))) {
                currentCmd = potentialCmd + " " + tokens.get(i + 1);
                i++; // Skip the next token since it's part of the command
                continue;
            } else if (currentCmd == null && registeredCommands.contains(potentialCmd)) {
                currentCmd = potentialCmd;
                continue;
            }

            if (token.equals("|")) {
                if (currentCmd != null) {
                    ParsedCommand parsed = parseCommand(resolveAlias(currentCmd, aliases), currentArgs, registeredCommands);
                    pipeline.add(parsed);
                    currentArgs.clear();
                    currentCmd = null;
                }
            } else if (token.equals(">")) {
                expectRedirect = true;
            } else if (expectRedirect) {
                if (currentCmd != null) {
                    ParsedCommand parsed = parseCommand(resolveAlias(currentCmd, aliases), currentArgs, registeredCommands);
                    parsed.redirectFile = token;
                    pipeline.add(parsed);
                    currentArgs.clear();
                    currentCmd = null;
                    expectRedirect = false;
                }
            } else if (currentCmd == null) {
                currentCmd = token;
            } else {
                currentArgs.add(token);
            }
        }

        if (currentCmd != null) {
            ParsedCommand parsed = parseCommand(resolveAlias(currentCmd, aliases), currentArgs, registeredCommands);
            pipeline.add(parsed);
        }

        return pipeline;
    }

    private static ParsedCommand parseCommand(String cmd, List<String> args, Set<String> registeredCommands) {
        // Split aliased command into command and additional args
        String[] parts = cmd.trim().split("\\s+", 2);
        String command = parts[0];
        List<String> allArgs = new ArrayList<>(args);
        if (parts.length > 1) {
            // Add arguments from the aliased command
            String[] aliasArgs = parts[1].split("\\s+");
            for (String arg : aliasArgs) {
                allArgs.add(0, arg); // Prepend alias args to maintain order
            }
        }
        return new ParsedCommand(command, allArgs);
    }

    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
            } else if ((c == '|' || c == '>') && !inQuotes) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else {
                token.append(c);
            }
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        return tokens;
    }

    private static String resolveAlias(String cmd, Map<String, String> aliases) {
        return aliases.getOrDefault(cmd, cmd);
    }
}
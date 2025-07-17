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

    public static List<ParsedCommand> parse(String input, Map<String, String> aliases) {
        List<ParsedCommand> pipeline = new ArrayList<>();
        List<String> tokens = tokenize(input);
        List<String> currentArgs = new ArrayList<>();
        String currentCmd = null;
        boolean expectRedirect = false;

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("|")) {
                if (currentCmd != null) {
                    pipeline.add(new ParsedCommand(resolveAlias(currentCmd, aliases), new ArrayList<>(currentArgs)));
                    currentArgs.clear();
                    currentCmd = null;
                }
            } else if (token.equals(">")) {
                expectRedirect = true;
            } else if (expectRedirect) {
                if (currentCmd != null) {
                    ParsedCommand cmd = new ParsedCommand(resolveAlias(currentCmd, aliases), new ArrayList<>(currentArgs));
                    cmd.redirectFile = token;
                    pipeline.add(cmd);
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
            pipeline.add(new ParsedCommand(resolveAlias(currentCmd, aliases), currentArgs));
        }

        return pipeline;
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
            } else if (c == '|' || c == '>' && !inQuotes) {
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
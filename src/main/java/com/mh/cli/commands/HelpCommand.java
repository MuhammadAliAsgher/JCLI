package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command {
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) {
            System.out.println("Available commands:");
            commands.keySet().stream().sorted().forEach(cmd -> System.out.println("  " + cmd));
        } else {
            String cmd = args.get(0).toLowerCase();
            if (commands.containsKey(cmd)) {
                System.out.println("Command: " + cmd);
                System.out.println(getHelpText(cmd));
            } else {
                throw new IOException("Unknown command: " + cmd);
            }
        }
    }

    private String getHelpText(String cmd) {
        return switch (cmd) {
            case "help" -> "help [command] - Show all commands or help for a specific command";
            case "echo" -> "echo [args] - Print arguments to console";
            case "pwd" -> "pwd - Print current working directory";
            case "cd" -> "cd [dir] - Change current directory (default: home)";
            case "exit" -> "exit - Exit the CLI";
            default -> "No help available";
        };
    }
}
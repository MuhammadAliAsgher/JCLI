package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class RmdirCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) throw new IOException("Usage: rmdir [dir]");
        Path path = shell.resolveSafePath(args.get(0));
        if (Files.isDirectory(path) && path.toFile().list().length == 0) {
            Files.delete(path);
        } else {
            throw new IOException("Directory not empty or not found: " + args.get(0));
        }
    }
}
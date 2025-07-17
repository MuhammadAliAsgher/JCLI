package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class CdCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        String dir = args.isEmpty() ? System.getProperty("user.home") : args.get(0);
        Path newPath = shell.resolveSafePath(dir);
        if (Files.isDirectory(newPath)) {
            shell.setCurrentDir(newPath.toString());
        } else {
            throw new IOException("Directory not found: " + dir);
        }
    }
}
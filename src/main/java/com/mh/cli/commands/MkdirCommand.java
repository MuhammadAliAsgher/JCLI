package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.nio.file.*;
import java.util.List;
import java.io.IOException;

public class MkdirCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) throw new IOException("Usage: mkdir [dir]");
        Path path = shell.resolveSafePath(args.get(0));
        Files.createDirectories(path);
    }
}
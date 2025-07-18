package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class RmCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) throw new IOException("Usage: rm [file]");
        Path path = shell.resolveSafePath(args.get(0));
        if (Files.isRegularFile(path)) Files.delete(path);
        else throw new IOException("Not a file: " + args.get(0));
    }
}
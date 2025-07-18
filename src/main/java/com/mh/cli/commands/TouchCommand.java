package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class TouchCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) throw new IOException("Usage: touch [file]");
        Path path = shell.resolveSafePath(args.get(0));
        if (!Files.exists(path)) Files.createFile(path);
    }
}
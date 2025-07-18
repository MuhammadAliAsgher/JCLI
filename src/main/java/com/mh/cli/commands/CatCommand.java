package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class CatCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty() && input.isEmpty()) throw new IOException("Usage: cat [file]");
        if (!input.isEmpty()) {
            System.out.println(input);
        } else {
            Path path = shell.resolveSafePath(args.get(0));
            Files.lines(path).forEach(System.out::println);
        }
    }
}
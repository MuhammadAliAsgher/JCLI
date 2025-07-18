package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class HeadCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        int n = 10;
        int fileIdx = 0;
        if (args.contains("-n") && args.size() > args.indexOf("-n") + 1) {
            n = Integer.parseInt(args.get(args.indexOf("-n") + 1));
            fileIdx = args.indexOf("-n") + 2;
        }
        if (fileIdx >= args.size() && input.isEmpty()) throw new IOException("Usage: head [-n N] [file]");
        if (!input.isEmpty()) {
            String[] lines = input.split("\n");
            for (int i = 0; i < Math.min(n, lines.length); i++) System.out.println(lines[i]);
        } else {
            Path path = shell.resolveSafePath(args.get(fileIdx));
            Files.lines(path).limit(n).forEach(System.out::println);
        }
    }
}
package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        boolean reverse = args.contains("-r");
        if (args.isEmpty() && input.isEmpty()) throw new IOException("Usage: sort [-r] [file]");
        List<String> lines;
        if (!input.isEmpty()) {
            lines = new ArrayList<>(List.of(input.split("\n")));
        } else {
            int fileIdx = reverse ? 1 : 0;
            if (fileIdx >= args.size()) throw new IOException("Usage: sort [-r] [file]");
            Path path = shell.resolveSafePath(args.get(fileIdx));
            lines = Files.readAllLines(path);
        }
        Collections.sort(lines);
        if (reverse) Collections.reverse(lines);
        lines.forEach(System.out::println);
    }
}
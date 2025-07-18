package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class WcCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) throw new IOException("Usage: wc [file]");
        Path path = shell.resolveSafePath(args.get(0));
        List<String> lines = Files.readAllLines(path);
        long words = lines.stream().flatMap(l -> java.util.Arrays.stream(l.split("\\s+"))).filter(w -> !w.isEmpty()).count();
        long chars = lines.stream().mapToLong(String::length).sum() + lines.size();
        System.out.printf("%d %d %d %s%n", lines.size(), words, chars, args.get(0));
    }
}
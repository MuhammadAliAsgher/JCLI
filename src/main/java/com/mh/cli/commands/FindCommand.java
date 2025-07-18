package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

public class FindCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.size() < 2 || !args.get(0).equals("-name")) throw new IOException("Usage: find -name pattern");
        String pattern = args.get(1).replace("*", ".*");
        Pattern p = Pattern.compile(pattern);
        Path start = Paths.get(shell.getCurrentDir());
        Files.walk(start).filter(f -> p.matcher(f.getFileName().toString()).matches())
            .forEach(f -> System.out.println(start.relativize(f)));
    }
}
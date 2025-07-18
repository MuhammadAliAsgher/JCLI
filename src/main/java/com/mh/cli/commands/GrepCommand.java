package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

public class GrepCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.size() < (input.isEmpty() ? 2 : 1)) throw new IOException("Usage: grep [-i -n] pattern [file]");
        boolean ignoreCase = args.contains("-i");
        boolean lineNum = args.contains("-n");
        
        // Find pattern and file arguments, skipping flags
        String pattern = null;
        String file = null;
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals("-i") || args.get(i).equals("-n")) {
                continue;
            }
            if (pattern == null) {
                pattern = args.get(i);
            } else if (file == null && input.isEmpty()) {
                file = args.get(i);
            }
        }
        if (pattern == null) throw new IOException("Usage: grep [-i -n] pattern [file]");

        Pattern p = ignoreCase ? Pattern.compile(pattern, Pattern.CASE_INSENSITIVE) : Pattern.compile(pattern);
        if (!input.isEmpty()) {
            String[] lines = input.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (p.matcher(lines[i]).find()) System.out.println((lineNum ? (i + 1) + ":" : "") + lines[i]);
            }
        } else {
            if (file == null) throw new IOException("Usage: grep [-i -n] pattern [file]");
            Path path = shell.resolveSafePath(file);
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                if (p.matcher(lines.get(i)).find()) System.out.println((lineNum ? (i + 1) + ":" : "") + lines.get(i));
            }
        }
    }
}
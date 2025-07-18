package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.io.File;

public class LsCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        boolean recursive = args.contains("-r");
        boolean longFormat = args.contains("-l");
        boolean all = args.contains("-a");
        Path dir = Paths.get(shell.getCurrentDir());
        File[] files = dir.toFile().listFiles(f -> all || !f.getName().startsWith("."));
        if (files == null) throw new IOException("Cannot access directory");
        List<File> fileList = Arrays.asList(files);
        if (recursive) Collections.reverse(fileList);
        for (File f : fileList) {
            if (longFormat) {
                String perms = (f.canRead() ? "r" : "-") + (f.canWrite() ? "w" : "-") + (f.canExecute() ? "x" : "-");
                System.out.printf("%s %10d %s %s%n", perms, f.length(), new Date(f.lastModified()), f.getName());
            } else {
                System.out.println(f.getName());
            }
        }
    }
}
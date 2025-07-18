package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class RmRecursiveCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) throw new IOException("Usage: rm -r [dir]");
        Path path = shell.resolveSafePath(args.get(0));
        if (!Files.isDirectory(path)) throw new IOException("Not a directory: " + args.get(0));
        System.out.print("Remove " + path + " and contents? (y/n): ");
        if (!new Scanner(System.in).nextLine().trim().toLowerCase().startsWith("y")) return;
        Files.walk(path).sorted(Comparator.reverseOrder()).forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                System.err.println("Delete failed: " + e.getMessage());
            }
        });
    }
}
package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;

public class MvCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.size() < 2) throw new IOException("Usage: mv [-i] [src] [dest]");
        boolean interactive = args.contains("-i");
        int srcIdx = args.size() - 2;
        int destIdx = args.size() - 1;
        Path src = shell.resolveSafePath(args.get(srcIdx));
        Path dest = shell.resolveSafePath(args.get(destIdx));
        if (interactive && Files.exists(dest)) {
            System.out.print("Overwrite " + dest + "? (y/n): ");
            if (!new Scanner(System.in).nextLine().trim().toLowerCase().startsWith("y")) return;
        }
        Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
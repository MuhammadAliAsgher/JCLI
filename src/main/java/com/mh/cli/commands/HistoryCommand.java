package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.util.List;

public class HistoryCommand implements Command {
    private final List<String> history;

    public HistoryCommand(List<String> history) {
        this.history = history;
    }

    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        int n = history.size();
        if (args.contains("-n") && args.size() > args.indexOf("-n") + 1) {
            n = Integer.parseInt(args.get(args.indexOf("-n") + 1));
        }
        if (args.contains("-c")) {
            history.clear();
            return;
        }
        int start = Math.max(0, history.size() - n);
        for (int i = start; i < history.size(); i++) {
            System.out.println((i + 1) + ": " + history.get(i));
        }
    }
}
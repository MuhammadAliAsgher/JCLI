package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Config;
import com.mh.cli.Shell;
import java.io.IOException;
import java.util.List;

public class AliasCommand implements Command {
    private final Config config;

    public AliasCommand(Config config) {
        this.config = config;
    }

    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        if (args.isEmpty()) {
            config.getAliases().forEach((k, v) -> System.out.println(k + "='" + v + "'"));
        } else {
            String[] parts = args.get(0).split("=", 2);
            if (parts.length != 2) throw new IOException("Usage: alias name=command");
            config.setAlias(parts[0], parts[1].replaceAll("^'|'$", ""));
        }
    }
}
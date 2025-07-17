package com.mh.cli.commands;

import com.mh.cli.Command;
import com.mh.cli.Shell;
import java.io.IOException;
import java.util.List;

public class EchoCommand implements Command {
    @Override
    public void execute(List<String> args, String input, Shell shell) throws IOException {
        System.out.println(String.join(" ", args));
    }
}
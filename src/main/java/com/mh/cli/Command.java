package com.mh.cli;

import java.io.IOException;
import java.util.List;

public interface Command {
    void execute(List<String> args, String input, Shell shell) throws IOException;
}
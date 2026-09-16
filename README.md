# JCLI

[![Java](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg?logo=apache-maven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/MuhammadAliAsgher/JCLI/ci.yml?branch=main&logo=github-actions&label=CI)](https://github.com/MuhammadAliAsgher/JCLI/actions/workflows/ci.yml)
[![Coverage](https://raw.githubusercontent.com/MuhammadAliAsgher/JCLI/main/.github/badges/jacoco.svg)](https://github.com/MuhammadAliAsgher/JCLI/actions/workflows/ci.yml)

A Unix-like command-line shell implemented from scratch in Java 21 — its own tokenizer, argument parser, and command dispatcher, not a wrapper around the OS shell. Supports piping, output redirection, aliases, and persistent history and config.

```
> ls -l | grep ".txt" | sort
-rw-rw-rw-      7 Mon Sep 15 21:03:11 GMT+05:00 2026 notes.txt
-rw-rw-rw-     41 Mon Sep 15 21:04:02 GMT+05:00 2026 todo.txt
> echo "backup complete" > log.txt
> alias ll="ls -l"
> ll
```

## Commands

24 built-in commands:

| Command | Syntax | Notes |
|---|---|---|
| `help` | `help [command]` | List all commands, or show one command's usage |
| `echo` | `echo [args]` | Print arguments |
| `pwd` | `pwd` | Print working directory |
| `cd` | `cd [dir]` | Change directory (no argument → home) |
| `ls` | `ls [-a] [-l] [-r]` | `-a` show hidden, `-l` long format (permissions/size/mtime), `-r` reverse the listing order |
| `cat` | `cat [file]` | Print file contents |
| `head` | `head [-n N] [file]` | First N lines (default 10) |
| `tail` | `tail [-n N] [file]` | Last N lines (default 10) |
| `grep` | `grep [-i] [-n] pattern [file]` | `-i` case-insensitive, `-n` show line numbers |
| `wc` | `wc [file]` | Line/word/char counts |
| `sort` | `sort [-r] [file]` | `-r` reverse order |
| `find` | `find -name pattern` | Find files by glob under the current directory |
| `touch` | `touch [file]` | Create a file, or update its timestamp |
| `mkdir` | `mkdir [dir]` | Create a directory |
| `rmdir` | `rmdir [dir]` | Remove an empty directory |
| `cp` | `cp [-r] [-i] src dest` | `-r` recurse into directories, `-i` prompt before overwriting |
| `mv` | `mv [-i] src dest` | `-i` prompt before overwriting |
| `rm` | `rm [file]` | Remove a file |
| `rm -r` | `rm -r [dir]` | Remove a directory and its contents, with a y/n confirmation prompt |
| `history` | `history [-n N] [-c]` | Show last N entries (default: all), `-c` clears history |
| `alias` | `alias [name=command]` | List aliases, or define one |
| `clear` | `clear` | Clear the terminal |
| `date` | `date` | Print the current date/time |
| `exit` | `exit` | Quit |

All file paths are resolved and checked against the current working directory — `../` traversal outside it is rejected.

## How it's built

```
Shell.run()                     <- REPL loop, owns the one Scanner(System.in)
    │
    ▼
ArgumentParser.parse(input, aliases, registeredCommands)
    │  tokenizes, resolves aliases, splits on | and >, groups multi-word
    │  commands (e.g. "rm -r") so they dispatch as a single unit
    ▼
Shell dispatches each ParsedCommand to its registered Command
    │
    ▼
Command.execute(args, pipedInput, shell)   <- one class per command
```

- `Shell` owns the command registry, current directory, history, and the single input `Scanner` — commands that need to read a confirmation (`cp -i`, `mv -i`, `rm -r`) go through `shell.readLine()` rather than opening their own `Scanner(System.in)`; two `Scanner`s on the same stream corrupt each other's buffering, which was a real bug here.
- `ArgumentParser` handles tokenizing (including quoted strings), alias resolution, `|` pipelines, and `>` redirection.
- `Config` persists aliases and the prompt format to `~/.clirc`.
- Command output between pipeline stages is captured by temporarily swapping `System.out`.

## Getting started

Requires JDK 21 and Maven 3.9+.

```bash
git clone https://github.com/MuhammadAliAsgher/JCLI.git
cd JCLI
mvn clean package
java -jar target/JCLI-1.0-SNAPSHOT.jar
```

## Configuration

`~/.clirc` stores aliases and the prompt format, and is created automatically the first time you set an alias:

```
prompt=%s $
alias ll='ls -l'
alias la='ls -a'
```

`%s` in `prompt` is replaced with the current directory. History persists to `~/.cli_history` (last 1000 entries).

## Testing

```bash
mvn test                              # run the suite
open target/site/jacoco/index.html    # coverage report, after `mvn test`
```

CI-verified on every push to `main` via JaCoCo (see the badge above): **93% instruction / 92% line coverage**.

- Every command has its own unit test file, run against a real `Shell` instance and real temporary files — no mocking library.
- `ShellIntegrationTest` and `ShellTest` exercise the REPL loop end-to-end: piping, redirection, unknown commands, error handling.
- Tests are isolated from your real environment via `Shell.forTesting()` — history and config both live in a throwaway temp directory, never your actual `~/.clirc` or `~/.cli_history`.

## Known limitations

- Interactive-only: no non-interactive mode (`jcli -c "cmd1; cmd2"` or a script file) yet.
- `exit` calls `System.exit(0)` directly, so it can't be intercepted or unit-tested.
- `ls -r` reverses the listing order — it does not recurse into subdirectories, despite the name.

## License

MIT — see [LICENSE](LICENSE).

<div align="center">

# 🖥️ JCLI - Java Command Line Interface

<img src="https://readme-typing-svg.herokuapp.com?font=JetBrains+Mono&size=24&duration=3000&pause=1000&color=2E9EF7&center=true&vCenter=true&width=600&lines=Welcome+to+JCLI!;Java+21+Powered+CLI;Unix-like+Commands;Cross-Platform+Terminal;Built+with+%E2%9D%A4%EF%B8%8F+and+Java" alt="Typing SVG" />

<p>
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</p>

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg?style=for-the-badge&logo=apache-maven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg?style=for-the-badge&logo=github-actions)]()

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="900">

</div>

<div align="center">

### 🌟 **If you find this project helpful, please consider giving it a star!** 
### Your support helps others discover this project and motivates continued development.

<img src="https://user-images.githubusercontent.com/74038190/212284158-e840e285-664b-44d7-b79b-e264b5e54825.gif" width="400">

</div>

<br>

<div align="center">

## 🚀 **A Powerful Unix-like CLI Built with Modern Java** 

*JCLI combines the familiarity of traditional shell commands with robust Java architecture, comprehensive testing, and cross-platform compatibility.*

</div>

<br>

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</div>

## 🚀 Project Overview

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223263-cf2e4b07-2615-4f87-9c38-e37600f8381a.gif" width="400">

JCLI is a comprehensive CLI shell implementation that recreates essential Unix/Linux commands in pure Java. It features a modular architecture, extensive testing suite, and advanced capabilities like command piping, output redirection, aliases, and persistent configuration.

<br clear="right">

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284136-03988914-d42b-4505-b9fe-f8a32f976499.gif" width="400">
</div>

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</div>

### ✨ Key Highlights

<table>
<tr>
<td>

🔥 **20+ Built-in Commands**  
Complete implementation of essential CLI tools

🔗 **Command Pipeline Support**  
Unix-style piping with `|` operator

📤 **Output Redirection**  
Redirect command output to files with `>`

⚙️ **Persistent Configuration**  
Aliases and settings stored in `~/.clirc`

</td>
<td>

📚 **Command History**  
Full history tracking with search capabilities

🛡️ **Path Security**  
Built-in protection against directory traversal attacks

🌐 **Cross-Platform**  
Runs on Windows, Linux, and macOS

⚡ **Native Compilation**  
GraalVM support for fast, native executables

</td>
</tr>
</table>

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284115-f47cd8ff-2ffb-4b04-b5bf-4d1c14c0247f.gif" width="1000">
</div>

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284158-e840e285-664b-44d7-b79b-e264b5e54825.gif" width="200">
</div>

## 📋 Features

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223156-0cbdaba9-3128-4d8e-8719-b6b4cf741b67.gif" width="300">

### Core Commands

| Command | Syntax | Description |
|---------|--------|-------------|
| `help` | `help [command]` | Show all commands or help for specific command |
| `echo` | `echo [args]` | Print arguments to console |
| `pwd` | `pwd` | Print current working directory |
| `cd` | `cd [dir]` | Change directory (default: home) |
| `ls` | `ls [-r -l -a]` | List directory contents with options |
| `cp` | `cp [-r -i] [src] [dest]` | Copy files/directories |
| `mv` | `mv [-i] [src] [dest]` | Move/rename files |
| `mkdir` | `mkdir [dir]` | Create directory |
| `rmdir` | `rmdir [dir]` | Remove empty directory |
| `rm` | `rm [file]` | Remove file |
| `rm -r` | `rm -r [dir]` | Remove directory recursively |
| `touch` | `touch [file]` | Create empty file or update timestamp |
| `cat` | `cat [file]` | Display file contents |
| `grep` | `grep [-i -n] [pattern] [file]` | Search text patterns |
| `wc` | `wc [file]` | Count lines, words, characters |
| `head` | `head [-n N] [file]` | Display first N lines (default: 10) |
| `tail` | `tail [-n N] [file]` | Display last N lines (default: 10) |
| `sort` | `sort [-r] [file]` | Sort lines alphabetically |
| `find` | `find [-name pattern]` | Find files matching pattern |
| `history` | `history [-c -n N]` | Show command history |
| `clear` | `clear` | Clear terminal screen |
| `date` | `date` | Display current date and time |
| `alias` | `alias [name=command]` | Create command aliases |
| `exit` | `exit` | Exit the CLI |

### Advanced Features

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</div>

#### 🔗 Command Piping
<img align="right" src="https://user-images.githubusercontent.com/74038190/229223263-cf2e4b07-2615-4f87-9c38-e37600f8381a.gif" width="250">

Chain commands together with Unix-style piping:
```bash
cat file.txt | grep "pattern" | sort | head -5
find . -name "*.txt" | head -10
ls -l | grep "2024"
```

#### 📤 Output Redirection
Redirect command output to files:
```bash
echo "Hello World" > output.txt
ls -l > directory_listing.txt
date > timestamp.txt
```

#### 🔗 Command Aliases
Create shortcuts for frequently used commands:
```bash
alias ll='ls -l'
alias la='ls -a'
alias grep-java='grep -i java'
```

#### ⚙️ Configuration Management
- **Config file**: `~/.clirc` stores aliases and prompt settings
- **Persistent aliases**: Automatically loaded on startup
- **Custom prompt**: Configurable command prompt format

#### 📚 Command History
- **Persistent storage**: History saved to `~/.cli_history`
- **Search capabilities**: `history -n 20` shows last 20 commands
- **History clearing**: `history -c` clears all history

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="600">
</div>

## 🛠️ Technical Details

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223156-0cbdaba9-3128-4d8e-8719-b6b4cf741b67.gif" width="300">

### Architecture
- **Language**: Java 21 with modern features (records, pattern matching, text blocks)
- **Build System**: Maven 3.9+ with comprehensive dependency management
- **Logging**: SLF4J with Logback for structured logging
- **Testing**: JUnit 5 with extensive unit and integration tests
- **Code Coverage**: JaCoCo integration for test coverage analysis

### Security Features
- **Path Validation**: Automatic prevention of directory traversal attacks
- **Safe File Operations**: All file operations validate paths against working directory
- **Input Sanitization**: Command arguments are properly validated and escaped

### Performance & Compatibility
- **Cross-Platform**: Native support for Windows, Linux, and macOS
- **Memory Efficient**: Optimized for low memory footprint
- **Fast Startup**: Sub-second startup time
- **Native Compilation**: GraalVM support for native executables

<br>

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</div>

## 📦 Prerequisites

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223263-cf2e4b07-2615-4f87-9c38-e37600f8381a.gif" width="350">

### Required Software

| Tool | Version | Verification Command |
|------|---------|---------------------|
| **JDK 21** | OpenJDK 21+ | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Git** | Latest | `git --version` |

### Development Environment

#### Recommended IDE: VS Code
Install VS Code from [code.visualstudio.com](https://code.visualstudio.com) with these extensions:
- **Java Extension Pack** - Complete Java development support
- **Maven for Java** - Maven project management
- **GitLens** - Enhanced Git capabilities (optional)

#### Optional: GraalVM (for native compilation)
- **GraalVM 21** - For creating native executables
- Enables ultra-fast startup and reduced memory usage



## 🚀 Installation & Setup

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223156-0cbdaba9-3128-4d8e-8719-b6b4cf741b67.gif" width="300">

### 1. Install Development Tools

#### JDK 21 Installation
```bash
# Linux (Ubuntu/Debian)
sudo apt install openjdk-21-jdk

# macOS (Homebrew)
brew install openjdk@21

# Windows
# Download from https://adoptium.net/
```

#### Maven Installation
```bash
# Linux
sudo apt install maven

# macOS
brew install maven

# Windows
# Download from https://maven.apache.org/download.cgi
```

#### Git Installation
```bash
# Linux
sudo apt install git

# macOS
brew install git

# Windows
# Download from https://git-scm.com/download/win
```

### 2. Clone and Build

```bash
# Clone the repository
git clone https://github.com/MuhammadAliAsgher/JCLI.git
cd JCLI

# Build the project
mvn clean compile

# Run tests
mvn test

# Create executable JAR
mvn package

# Run the application
java -jar target/JCLI-1.0-SNAPSHOT.jar
```

### 3. Development Setup

#### VS Code Configuration
1. Open project in VS Code: `code .`
2. Install recommended extensions when prompted
3. VS Code will automatically configure Java classpath and build settings

#### Maven Integration
- **Compile**: `mvn compile`
- **Test**: `mvn test`
- **Package**: `mvn package`
- **Clean**: `mvn clean`

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212257468-1e9a91f1-b626-4baa-b15d-5c385b7ca7d2.gif" width="100">
</div>

## 🎯 Usage Examples

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223263-cf2e4b07-2615-4f87-9c38-e37600f8381a.gif" width="300">

### Basic File Operations
```bash
# Navigate and explore
pwd
ls -la
cd Documents
mkdir projects
cd projects

# Create and edit files
touch README.md
echo "# My Project" > README.md
cat README.md
```

### Text Processing Pipelines
```bash
# Find and filter files
find . -name "*.java" | head -10
ls -l | grep "txt" | sort

# Process file contents
cat logfile.txt | grep "ERROR" | wc
head -20 data.csv | tail -5
```

### Command Aliases
```bash
# Create useful shortcuts
alias ll='ls -l'
alias la='ls -a'
alias projects='cd ~/Documents/projects'

# Use aliases
ll
la
projects
```

### History and Navigation
```bash
# View command history
history
history -n 10

# Clear screen and history
clear
history -c
```

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</div>

## 🧪 Testing

<img align="right" src="https://user-images.githubusercontent.com/74038190/229223156-0cbdaba9-3128-4d8e-8719-b6b4cf741b67.gif" width="250">

The project includes comprehensive testing coverage:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ShellIntegrationTest

# Generate coverage report
mvn jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage
- **Unit Tests**: Individual command testing with mocks
- **Integration Tests**: End-to-end workflow validation
- **Security Tests**: Path traversal and input validation
- **Performance Tests**: Memory and execution time validation

## 🔧 Advanced Configuration

### Custom Aliases
Edit `~/.clirc` to add persistent aliases:
```bash
alias ll='ls -l'
alias la='ls -a'
alias grep-java='grep -i java'
prompt=%s $
```

### Environment Variables
- `JCLI_HOME`: Override default configuration directory
- `JCLI_HISTORY_SIZE`: Set maximum history entries (default: 1000)

### Native Compilation (GraalVM)
```bash
# Install GraalVM native-image
gu install native-image

# Create native executable
native-image -jar target/JCLI-1.0-SNAPSHOT.jar jcli

# Run native executable
./jcli
```

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="800">
</div>

## 🤝 Contributing

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284087-bbe7e430-757e-4901-90bf-4cd2ce3e1852.gif" width="100">
</div>

We welcome contributions! Here's how to get started:

1. **Fork the repository** on GitHub
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** with appropriate tests
4. **Commit changes**: `git commit -m 'Add amazing feature'`
5. **Push to branch**: `git push origin feature/amazing-feature`
6. **Open a Pull Request** with detailed description

### Development Guidelines
- Follow Java coding standards and conventions
- Add tests for new features and bug fixes
- Update documentation for user-facing changes
- Ensure all tests pass before submitting PR

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by Unix/Linux command-line tools
- Built with modern Java 21 features
- Comprehensive testing with JUnit 5
- Maven build system and dependency management

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284115-f47cd8ff-2ffb-4b04-b5bf-4d1c14c0247f.gif" width="1000">
</div>

---

<div align="center">
<img src="https://user-images.githubusercontent.com/74038190/212284158-e840e285-664b-44d7-b79b-e264b5e54825.gif" width="400">

<img src="https://readme-typing-svg.herokuapp.com?font=JetBrains+Mono&size=20&duration=2000&pause=1000&color=FFD700&center=true&vCenter=true&width=500&lines=Thank+you+for+visiting!;%E2%AD%90+Star+this+repo+if+it+helped!;Happy+Coding!+%F0%9F%9A%80" alt="Typing SVG" />

### **⭐ Star this repository if it helped you! Your support makes a difference. ⭐**

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="900">

</div>

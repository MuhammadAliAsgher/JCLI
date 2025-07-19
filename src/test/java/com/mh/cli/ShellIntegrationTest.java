package com.mh.cli;

import com.mh.cli.commands.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Java CLI Shell.
 * 
 * These tests verify that commands work together properly in realistic scenarios,
 * simulating actual command pipelines and workflows that users would perform.
 * 
 * NOTE: We avoid testing shell.run() with exit commands because ExitCommand calls System.exit()
 * which terminates the JVM and causes Maven Surefire to crash. Instead, we test command 
 * interactions directly using the Command interface.
 */
class ShellIntegrationTest {
    private Shell shell;
    private Path tempDir;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws IOException {
        shell = new Shell();
        tempDir = Files.createTempDirectory("cli-integration-test");
        shell.setCurrentDir(tempDir.toString());
        
        // Redirect System.out to capture command output
        out = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    void testBasicFileWorkflow() throws IOException {
        // Simulate: touch file.txt && echo "content" > file.txt && cat file.txt
        
        // Create file
        TouchCommand touch = new TouchCommand();
        touch.execute(List.of("test.txt"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("test.txt")));
        
        // Write content (simulating echo with redirection)
        Files.write(tempDir.resolve("test.txt"), "Hello World\nSecond line\n".getBytes());
        
        // Read content back
        out.reset();
        CatCommand cat = new CatCommand();
        cat.execute(List.of("test.txt"), "", shell);
        String output = out.toString().replace("\r\n", "\n");
        assertEquals("Hello World\nSecond line\n", output);
    }

    @Test
    void testPipelineSimulation() throws IOException {
        // Simulate: cat data.txt | grep "pattern" | wc
        
        // Setup test file
        Files.write(tempDir.resolve("data.txt"), 
            "Line 1 with pattern\nLine 2 without\nAnother pattern line\nJust text\n".getBytes());
        
        // Step 1: cat file.txt
        out.reset();
        CatCommand cat = new CatCommand();
        cat.execute(List.of("data.txt"), "", shell);
        String catOutput = out.toString().replace("\r\n", "\n");
        
        // Step 2: Use grep on the file directly (more realistic)
        out.reset();
        GrepCommand grep = new GrepCommand();
        grep.execute(List.of("pattern", "data.txt"), "", shell);
        String grepOutput = out.toString().replace("\r\n", "\n");
        assertEquals("Line 1 with pattern\nAnother pattern line\n", grepOutput);
        
        // Step 3: Count lines manually (simulating wc pipeline)
        String[] lines = grepOutput.trim().split("\n");
        assertEquals(2, lines.length, "Should find 2 lines matching 'pattern'");
    }

    @Test
    void testDirectoryOperations() throws IOException {
        // Simulate: mkdir project && cd project && touch README.md && ls
        
        // Create directory
        MkdirCommand mkdir = new MkdirCommand();
        mkdir.execute(List.of("project"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("project")));
        assertTrue(Files.isDirectory(tempDir.resolve("project")));
        
        // Change to directory
        CdCommand cd = new CdCommand();
        cd.execute(List.of("project"), "", shell);
        assertEquals(tempDir.resolve("project").toString(), shell.getCurrentDir());
        
        // Create file in new directory
        TouchCommand touch = new TouchCommand();
        touch.execute(List.of("README.md"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("project").resolve("README.md")));
        
        // List directory contents
        out.reset();
        LsCommand ls = new LsCommand();
        ls.execute(List.of(), "", shell);
        String lsOutput = out.toString().replace("\r\n", "\n");
        assertTrue(lsOutput.contains("README.md"));
    }

    @Test
    void testSortingWorkflow() throws IOException {
        // Simulate: cat unsorted.txt | sort | head -3
        
        // Setup unsorted data
        Files.write(tempDir.resolve("unsorted.txt"), 
            "zebra\napple\nbanana\ncherry\ndate\n".getBytes());
        
        // Step 1: cat
        out.reset();
        CatCommand cat = new CatCommand();
        cat.execute(List.of("unsorted.txt"), "", shell);
        String catOutput = out.toString().replace("\r\n", "\n");
        
        // Step 2: sort
        out.reset();
        SortCommand sort = new SortCommand();
        sort.execute(List.of(), catOutput, shell);
        String sortOutput = out.toString().replace("\r\n", "\n");
        
        // Step 3: head -3 (first 3 lines)
        out.reset();
        HeadCommand head = new HeadCommand();
        head.execute(List.of("-n", "3"), sortOutput, shell);
        String headOutput = out.toString().replace("\r\n", "\n");
        
        // Should be the first 3 alphabetically sorted items
        assertEquals("apple\nbanana\ncherry\n", headOutput);
    }

    @Test
    void testFileOperations() throws IOException {
        // Simulate: cp source.txt backup.txt && modify source && compare
        
        // Create original file
        Files.write(tempDir.resolve("source.txt"), "Original content\n".getBytes());
        
        // Copy file
        CpCommand cp = new CpCommand();
        cp.execute(List.of("source.txt", "backup.txt"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("backup.txt")));
        
        // Modify original
        Files.write(tempDir.resolve("source.txt"), "Modified content\n".getBytes());
        
        // Verify they're different now
        out.reset();
        CatCommand cat = new CatCommand();
        cat.execute(List.of("source.txt"), "", shell);
        String sourceContent = out.toString().replace("\r\n", "\n");
        
        out.reset();
        cat.execute(List.of("backup.txt"), "", shell);
        String backupContent = out.toString().replace("\r\n", "\n");
        
        assertNotEquals(sourceContent, backupContent);
        assertEquals("Modified content\n", sourceContent);
        assertEquals("Original content\n", backupContent);
    }

    @Test
    void testSearchAndFind() throws IOException {
        // Simulate: find -name "*.txt" | head -3
        
        // Create multiple files
        Files.write(tempDir.resolve("doc1.txt"), "Content 1".getBytes());
        Files.write(tempDir.resolve("doc2.txt"), "Content 2".getBytes());
        Files.write(tempDir.resolve("readme.md"), "Markdown content".getBytes());
        Files.write(tempDir.resolve("notes.txt"), "Notes content".getBytes());
        
        // Find txt files
        out.reset();
        FindCommand find = new FindCommand();
        find.execute(List.of("-name", "*.txt"), "", shell);
        String findOutput = out.toString().replace("\r\n", "\n");
        
        // Should find all .txt files but not .md files
        assertTrue(findOutput.contains("doc1.txt"));
        assertTrue(findOutput.contains("doc2.txt"));
        assertTrue(findOutput.contains("notes.txt"));
        assertFalse(findOutput.contains("readme.md"));
        
        // Limit results with head
        out.reset();
        HeadCommand head = new HeadCommand();
        head.execute(List.of("-n", "2"), findOutput, shell);
        String limitedOutput = out.toString().replace("\r\n", "\n");
        
        // Should only show first 2 results
        String[] lines = limitedOutput.trim().split("\n");
        assertEquals(2, lines.length);
    }

    @Test
    void testWorkingDirectoryNavigation() throws IOException {
        // Test pwd and basic file operations
        
        // Check current directory
        out.reset();
        PwdCommand pwd = new PwdCommand();
        pwd.execute(List.of(), "", shell);
        String currentDir = out.toString().replace("\r\n", "\n").trim();
        assertEquals(tempDir.toString(), currentDir);
        
        // Create subdirectory and verify it exists
        MkdirCommand mkdir = new MkdirCommand();
        mkdir.execute(List.of("subdir"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("subdir")));
        assertTrue(Files.isDirectory(tempDir.resolve("subdir")));
        
        // Create files in both current and subdirectory
        TouchCommand touch = new TouchCommand();
        touch.execute(List.of("mainfile.txt"), "", shell);
        assertTrue(Files.exists(tempDir.resolve("mainfile.txt")));
        
        // Create file in subdirectory using direct file operations
        Files.write(tempDir.resolve("subdir").resolve("subfile.txt"), "content".getBytes());
        assertTrue(Files.exists(tempDir.resolve("subdir").resolve("subfile.txt")));
        
        // Use ls command to list current directory and just verify it doesn't error
        out.reset();
        LsCommand ls = new LsCommand();
        ls.execute(List.of(), "", shell);
        String currentListing = out.toString().replace("\r\n", "\n");
        // Just verify ls produces some output (not empty)
        assertFalse(currentListing.trim().isEmpty(), "ls command should produce output for non-empty directory");
        
        // Test ls on subdirectory
        out.reset();
        ls.execute(List.of("subdir"), "", shell);
        String subdirListing = out.toString().replace("\r\n", "\n");
        // Verify ls on subdirectory produces output
        assertFalse(subdirListing.trim().isEmpty(), "ls command should produce output for subdirectory with files");
    }

    @Test
    void testEmptyFileHandling() throws IOException {
        // Test pipeline behavior with empty files
        
        // Create empty file
        Files.write(tempDir.resolve("empty.txt"), "".getBytes());
        
        // cat empty.txt
        out.reset();
        CatCommand cat = new CatCommand();
        cat.execute(List.of("empty.txt"), "", shell);
        String emptyContent = out.toString().replace("\r\n", "\n");
        assertEquals("", emptyContent);
        
        // Create a test file with content for grep testing
        Files.write(tempDir.resolve("test.txt"), "line1\nline2\n".getBytes());
        
        // grep on a file that exists but doesn't match pattern
        out.reset();
        GrepCommand grep = new GrepCommand();
        grep.execute(List.of("nonexistent", "test.txt"), "", shell);
        String grepResult = out.toString().replace("\r\n", "\n");
        // Should return empty since pattern doesn't match
        assertEquals("", grepResult);
        
        // wc on empty file should show 0 lines
        out.reset();
        WcCommand wc = new WcCommand();
        wc.execute(List.of("empty.txt"), "", shell);
        String wcOutput = out.toString().replace("\r\n", "\n");
        assertTrue(wcOutput.contains("0")); // Should show 0 lines
    }

    @AfterEach
    void tearDown() throws IOException {
        // Restore original System.out
        System.setOut(originalOut);
        
        // Clean up temporary directory
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .forEach(path -> {
                 try {
                     Files.deleteIfExists(path);
                 } catch (IOException e) {
                     // Ignore cleanup errors
                 }
             });
        
        // Clean up any CLI config files that might have been created during tests
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.home"), ".clirc"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.home"), ".cli_history"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
}

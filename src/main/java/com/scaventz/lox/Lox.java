package com.scaventz.lox;

import com.scaventz.lox.exception.RuntimeError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.scaventz.lox.Lox.ErrorLevel.ERROR;

public class Lox {
    private static final Interpreter interpreter = new Interpreter();

    public static boolean hadError = false;
    public static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            // exit codes stick to the conventions defined in the UNIX "sysexits.h" header
            // see https://www.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE&format=html
            System.out.println(64); // EX_USAGE (64)
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.println("> ");
            // To kill an interactive command-line app, you usually type Control-D. Doing so signals an "end-of-file" condition to the program.
            // When that happens readLine() returns null, so we check for that to exit the loop
            final String line = reader.readLine();
            if (line == null) break;
            run(line);
            // We need to reset this flag in the interactive loop. If the user makes a mistake, it shouldn’t kill their entire session.
            hadError = false;
        }
    }

    private static void run(String source) {
        final Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        final Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        // stop if there was a syntax error
        if (hadError) return;

        new Resolver(interpreter).resolve(statements);
        interpreter.interpret(statements);
    }

    static void error(int line, int column, String message) {
        report(line, column, ERROR, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, token.column, ERROR, " at end", message);
        } else {
            report(token.line, token.column, ERROR, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + ", column " + error.token.column + " ]");
        hadRuntimeError = true;
    }

    private static void report(int line, int column, ErrorLevel level, String where, String message) {
        System.err.println("[line " + line + ", column " + column + "] " + level.level + where + ": " + message);
        hadError = true;
    }

    enum ErrorLevel {
        WARNING("warning"),
        ERROR("error");

        private final String level;

        ErrorLevel(String level) {
            this.level = level;
        }
    }

    public static void stateReset() {
        hadError = false;
        hadRuntimeError = false;
    }
}
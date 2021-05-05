package com.scaventz.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Xin Wang
 * @date 5/6/2021
 */
public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) {
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
        if (hadError) System.exit(65); // EX_DATAERR (65)
    }

    private static void runPrompt() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.println("> ");
            // To kill an interactive command-line app, you usually type Control-D. Doing so signals an “end-of-file” condition to the program.
            // When that happens readLine() returns null, so we check for that to exit the loop
            final String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            // We need to reset this flag in the interactive loop. If the user makes a mistake, it shouldn’t kill their entire session.
            hadError = false;
        }
    }

    private static void run(String source) {
        final Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.out.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }


}
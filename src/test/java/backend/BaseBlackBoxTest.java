package backend;

import com.scaventz.lox.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseBlackBoxTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // TODO re-write and simplify relevant tests
    // TODO Note Stmt is not a public type, which requires re-write this test
    void runAndAssert(String source, String expected) {
        Lox.hadError = false;
        Lox.hadRuntimeError = false;

        List<Token> tokens = new Scanner(source).scanTokens();
        List<Stmt> statements = new Parser(tokens).parse();

        Interpreter interpreter = new Interpreter();
        new Resolver(interpreter).resolve(statements);

        if (Lox.hadError || Lox.hadRuntimeError) {
            assertEquals(expected, errContent.toString(StandardCharsets.UTF_8).replace("\r", ""));
        } else {
            interpreter.interpret(statements);

            if (Lox.hadError || Lox.hadRuntimeError) {
                assertEquals(expected, errContent.toString(StandardCharsets.UTF_8).replace("\r", ""));
            } else {
                String actual = outContent.toString().replace("\r", "");
                assertEquals(expected, actual);
            }
        }
    }
}

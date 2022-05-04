package scanner.unitTest;

import com.scaventz.lox.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvironmentTest {
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

    @Test
    public void smoke() {
        String source = "var a = 1;var b = 2;print a + b;";

        List<Token> tokens = new Scanner(source).scanTokens();
        new Interpreter().interpret(new Parser(tokens).parse());

        assertEquals("",errContent.toString(StandardCharsets.UTF_8));
        assert !Lox.hadError;
        assert !Lox.hadRuntimeError;
        assertEquals("3\r\n", outContent.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void assignment() {
        String source = "var a = 1;a = 2;print a;";

        List<Token> tokens = new Scanner(source).scanTokens();
        new Interpreter().interpret(new Parser(tokens).parse());

        assertEquals("",errContent.toString(StandardCharsets.UTF_8));
        assert !Lox.hadError;
        assert !Lox.hadRuntimeError;
        assertEquals("2\r\n", outContent.toString(StandardCharsets.UTF_8));
    }
}

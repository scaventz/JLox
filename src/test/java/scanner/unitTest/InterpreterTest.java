package scanner.unitTest;

import com.scaventz.lox.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
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
    public void printString() throws UnsupportedEncodingException {
        runAndAssert("print \"one\";", "one\r\n");
    }

    @Test
    public void printBoolean() throws UnsupportedEncodingException {
        runAndAssert("print true;", "true\r\n");
    }

    @Test
    public void printArithmeticResult() throws UnsupportedEncodingException {
        runAndAssert("print 2 + 1;", "3.0\r\n");
    }

    @Test
    public void testAssignment() throws UnsupportedEncodingException {
        String src = """
                var a = 5;
                a=6;
                print a;
                """;
        runAndAssert(src, "6\r\n");
    }

    // TODO re-write and simplify relevant tests
    // TODO Note Stmt is not a public type, which requires re-write this test
    private void runAndAssert(String source, String expected) throws UnsupportedEncodingException {
        List<Token> tokens = new Scanner(source).scanTokens();
        new Interpreter().interpret(new Parser(tokens).parse());

        assertEquals("", errContent.toString("UTF8"));
        assert !Lox.hadError;
        assert !Lox.hadRuntimeError;
        assertEquals(expected, outContent.toString("UTF8"));
    }
}

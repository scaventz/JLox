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
        runAndAssert("print \"one\";", "one");
    }

    @Test
    public void printBoolean() throws UnsupportedEncodingException {
        runAndAssert("print true;", "true");
    }

    @Test
    public void printArithmeticResult() throws UnsupportedEncodingException {
        runAndAssert("print 2 + 1;", "3");
    }

    @Test
    public void testAssignment() throws UnsupportedEncodingException {
        String src = """
                var a = 5;
                a=6;
                print a;
                """;
        runAndAssert(src, "6");
    }

    @Test
    public void testFunctionCall() throws UnsupportedEncodingException {
        String src = """
                fun greetTo(name) {
                    return "greetings, Mr "+name;
                }
                var message = greetTo("Anderson");
                print message;
                """;
        runAndAssert(src, "greetings, Mr Anderson");
    }

    @Test
    public void testReturn() throws UnsupportedEncodingException {
        String src = """
                    fun fib(n) {
                      if (n <= 1) return n;
                      return fib(n - 2) + fib(n - 1);
                    }
                    
                    for (var i = 0; i < 20; i = i + 1) {
                      print fib(i);
                    }
                """;
        String expect = "01123581321345589144233377610987159725844181";
        runAndAssert(src, expect);
    }

    @Test
    public void testLocalFunction() throws UnsupportedEncodingException {
        String src = """
                fun makeCounter() {
                  var i = 0;
                  fun count() {
                    i = i + 1;
                    print i;
                  }
                            
                  return count;
                }
                            
                var counter = makeCounter();
                counter();
                counter();
                    """;
        String expect = "12";
        runAndAssert(src, expect);
    }

    @Test
    public void testClosure() throws UnsupportedEncodingException {
        String src = """
                var a = "global";
                {
                  fun showA() {
                    print a;
                  }

                  showA();
                  var a = "block";
                  showA();
                }
                """;
        String expect = "globalglobal";
        runAndAssert(src, expect);
    }


    // TODO re-write and simplify relevant tests
    // TODO Note Stmt is not a public type, which requires re-write this test
    private void runAndAssert(String source, String expected) throws UnsupportedEncodingException {
        List<Token> tokens = new Scanner(source).scanTokens();
        List<Stmt> statements = new Parser(tokens).parse();

        Interpreter interpreter = new Interpreter();
        new Resolver(interpreter).resolve(statements);
        interpreter.interpret(statements);

        assertEquals("", errContent.toString("UTF8"));
        assert !Lox.hadError;
        assert !Lox.hadRuntimeError;
        String actual = outContent.toString().replace("\r", "");
        assertEquals(expected, actual);
    }
}

package box;

import com.scaventz.lox.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
    public void printString() {
        runAndAssert("print \"one\";", "one");
    }

    @Test
    public void printBoolean() {
        runAndAssert("print true;", "true");
    }

    @Test
    public void printArithmeticResult() {
        runAndAssert("print 2 + 1;", "3");
    }

    @Test
    public void testAssignment() {
        String src = """
                var a = 5;
                a=6;
                print a;
                """;
        runAndAssert(src, "6");
    }

    @Test
    public void testFunctionCall() {
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
    public void testReturn() {
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
    public void testLocalFunction() {
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
    public void testClosure() {
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

    @Test
    public void testDuplicateDeclaration() {
        String src = """
                fun bad() {
                  var a = "first";
                  var a = "second";
                }
                """;
        String expect = "[line 3, column 39] error at 'a': Already a variable with this name in this scope.\n";
        runAndAssert(src, expect);
    }

    @Test
    public void forLoop() {
        String src = """
                var a = 0;
                var temp;
                            
                for (var b = 1; a < 1000; b = temp + b) {
                  print a;
                  temp = a;
                  a = b;
                }
                """;

        String expect = "01123581321345589144233377610987";
        runAndAssert(src, expect);
    }

    @Test
    public void scopeTest() {
        String src = """
                var a = "global a";
                var b = "global b";
                var c = "global c";
                {
                  var a = "outer a";
                  var b = "outer b";
                  {
                    var a = "inner a";
                    print a;
                    print b;
                    print c;
                  }
                  print a;
                  print b;
                  print c;
                }
                print a;
                print b;
                print c;
                """;
        String expect = "inner aouter bglobal couter aouter bglobal cglobal aglobal bglobal c";
        runAndAssert(src, expect);
    }

    @Test
    public void functionToStringTest() {
        String src = """
                fun add(a, b) {
                    print a + b;
                }
                                
                print add; // "<fn add>".
                """;

        String expect = "<fn add>";
        runAndAssert(src, expect);
    }

    @Test
    public void functionInvocation() {
        String src = """
                fun sayHi(first, last) {
                  print "Hi, " + first + " " + last + "!";
                }
                                
                sayHi("Dear", "Reader");
                """;

        String expect = "Hi, Dear Reader!";
        runAndAssert(src, expect);
    }

    @Test
    public void functionReturn() {
        String src = """
                fun add(a, b) {
                    return a + b;
                }
                                
                print add(1,2); // "<fn add>".
                """;

        String expect = "3";
        runAndAssert(src, expect);
    }

    @Test
    public void functionNesting() {
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
                counter(); // "1".
                counter(); // "2".
                """;

        String expect = "12";
        runAndAssert(src, expect);
    }

    // TODO re-write and simplify relevant tests
    // TODO Note Stmt is not a public type, which requires re-write this test
    private void runAndAssert(String source, String expected) {
        Lox.hadError = false;
        List<Token> tokens = new Scanner(source).scanTokens();
        List<Stmt> statements = new Parser(tokens).parse();

        Interpreter interpreter = new Interpreter();
        new Resolver(interpreter).resolve(statements);
        interpreter.interpret(statements);

        if (Lox.hadError) {
            assertEquals(expected, errContent.toString(StandardCharsets.UTF_8).replace("\r", ""));
        }
        else {
            assert !Lox.hadRuntimeError;
            String actual = outContent.toString().replace("\r", "");
            assertEquals(expected, actual);
        }
    }
}

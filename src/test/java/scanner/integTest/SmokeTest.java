package scanner.integTest;

import com.scaventz.lox.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmokeTest {

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
    public void forLoop() throws UnsupportedEncodingException {
        String source = """
                var a = 0;
                var temp;
                            
                for (var b = 1; a < 1000; b = temp + b) {
                  print a;
                  temp = a;
                  a = b;
                }
                """;

        List<Token> tokens = new Scanner(source).scanTokens();
        new Interpreter().interpret(new Parser(tokens).parse());

        assert !Lox.hadError;
        assert !Lox.hadRuntimeError;
        String expected = """
                0
                1
                1
                2
                3
                5
                8
                13
                21
                34
                55
                89
                144
                233
                377
                610
                987
                """;
        assertEquals(expected, outContent.toString("UTF8").replace("\r", ""));
    }

    @Test
    public void scopeTest() throws UnsupportedEncodingException {
        String source = """
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

        List<Token> tokens = new Scanner(source).scanTokens();
        new Interpreter().interpret(new Parser(tokens).parse());

        assert !Lox.hadError;
        assert !Lox.hadRuntimeError;

        String expected = """
                inner a
                outer b
                global c
                outer a
                outer b
                global c
                global a
                global b
                global c
                """;
        assertEquals(expected, outContent.toString("UTF8").replace("\r", ""));
    }
}

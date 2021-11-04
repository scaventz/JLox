package scanner.unitTest;

import com.scaventz.lox.Lox;
import com.scaventz.lox.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerUnitTest {

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
    public void illegalCharacters() throws UnsupportedEncodingException {
        String source = "@#^";
        Scanner scanner = new Scanner(source);
        scanner.scanTokens();

        assert Lox.hadError;
        assertEquals(
                "[line 1, column 2] error: Unexpected character '@'.\r\n" +
                        "[line 1, column 3] error: Unexpected character '#'.\r\n" +
                        "[line 1, column 4] error: Unexpected character '^'.\r\n", errContent.toString("UTF8"));
    }

    @Test
    public void expectNoError() throws UnsupportedEncodingException {
        String source = "// this is a comment\n" +
                "(( )){} // grouping stuff\n" +
                "!*+-/=<> <= == // operators\n" +
                ".1234\n";
        Scanner scanner = new Scanner(source);
        scanner.scanTokens();

        assert !Lox.hadError;
        assertEquals("", errContent.toString("UTF8"));
    }

    @Test
    public void unterminatedString() throws UnsupportedEncodingException {
        String source = "\"unterminated string";
        Scanner scanner = new Scanner(source);
        scanner.scanTokens();

        assert Lox.hadError;
        assertEquals("[line 1, column 21] error: Unterminated string.\r\n", errContent.toString("UTF8"));
    }
}

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

public class ParserTest {

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
    public void testUnclosedBracket() throws UnsupportedEncodingException {
        String source = "a=(1+;";
        final Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        final Parser parser = new Parser(tokens);
        parser.parse();

        assert Lox.hadError;
        assertEquals(
                "[line 1, column 7] error at end: Expect expression.\r\n", errContent.toString("UTF8"));
    }
}

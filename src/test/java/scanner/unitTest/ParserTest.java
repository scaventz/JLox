package scanner.unitTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

    // TODO fixes this test
    /*@Test
    public void unclosedBracket() throws UnsupportedEncodingException {
        String source = "var a=(1+;";
        final Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        final Parser parser = new Parser(tokens);
        parser.parse();

        assert Lox.hadError;
        assertEquals(
                "[line 1, column 7] error at end: Expect expression.\r\n", errContent.toString("UTF8"));
    }*/
}

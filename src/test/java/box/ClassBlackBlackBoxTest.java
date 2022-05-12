package box;

import org.junit.jupiter.api.Test;

public class ClassBlackBlackBoxTest extends BaseBlackBoxTest {
    @Test
    public void testTestToStringMethod() {
        String src = """
                class DevonshireCream {
                  serveOn() {
                    return "Scones";
                  }
                }
                                
                print DevonshireCream; // Prints "DevonshireCream".
                """;
        runAndAssert(src, "DevonshireCream");
    }

    @Test
    public void testInstance() {
        String src = """
                class Bagel {}
                var bagel = Bagel();
                print bagel; // Prints "Bagel instance".
                """;
        runAndAssert(src, "Bagel instance");
    }
}

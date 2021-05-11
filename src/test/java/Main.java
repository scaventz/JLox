import com.scaventz.lox.Lox;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Xin Wang
 * @date 5/9/2021
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("src/test/resources/source.lox");
        System.out.println(file.exists());
        String[] loxArgs = new String[]{file.getCanonicalPath()};
        Lox.main(loxArgs);
    }
}
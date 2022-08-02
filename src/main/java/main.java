import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.GeneralSecurityException;

public class main {
  public static void main(String[] args) throws IOException, GeneralSecurityException {
    Readable readable = new InputStreamReader(System.in);
    Appendable appendable = System.out;

    SheetsAndJava sheetsAndJava = new SheetsAndJava("Erika's Tangela 79/132\n" +
            "Zubat 57/62\n" +
            "Shaymin EX RC21/RC25\n");
    TextBasedView view = new TextBasedView(appendable);
    TextBasedController controller = new TextBasedController(sheetsAndJava, view, readable);
    controller.start();
  }
}

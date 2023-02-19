import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.GeneralSecurityException;

public class main {
  public static void main(String[] args) throws IOException, GeneralSecurityException {
    Readable readable = new InputStreamReader(System.in);
    Appendable appendable = System.out;

    SheetsAndJava sheetsAndJava = new SheetsAndJava("Pickachu");
    TextBasedView view = new TextBasedView(appendable);
    TextBasedController controller = new TextBasedController(sheetsAndJava, view, readable);
    controller.start();
  }
}

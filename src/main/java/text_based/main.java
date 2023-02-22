import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import controller.SheetsAndJava;
import controller.TextBasedController;
import view.TextBasedView;

/**
 * The main class that runs the program on the command line. This class initializes the
 * necessary objects and starts the controller to manage user input/output through the console.
 */
public class main {
  /**
   * The main method of the program. Initializes the required objects and starts the controller
   * for managing user input/output through the console.
   *
   * @param args The command line arguments (not used in this program).
   * @throws IOException              if an I/O error occurs.
   * @throws GeneralSecurityException if a security error occurs.
   */
  public static void main(String[] args) throws IOException, GeneralSecurityException {
    Readable readable = new InputStreamReader(System.in);
    Appendable appendable = System.out;

    SheetsAndJava sheetsAndJava = new SheetsAndJava("Pickachu");
    TextBasedView view = new TextBasedView(appendable);
    TextBasedController controller = new TextBasedController(sheetsAndJava, view, readable);
    controller.start();
  }
}

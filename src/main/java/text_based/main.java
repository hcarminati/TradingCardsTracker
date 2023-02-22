package text_based;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import text_based.controller.SheetsAndJava;
import text_based.controller.TextBasedController;
import text_based.view.TextBasedView;

/**
 * The text_based.main class that runs the program on the command line. This class initializes the
 * necessary objects and starts the text_based.controller to manage user input/output through the console.
 */
public class main {
  /**
   * The text_based.main method of the program. Initializes the required objects and starts the text_based.controller
   * for managing user input/output through the console.
   *
   * @param args The command line arguments (not used in this program).
   * @throws IOException              if an I/O error occurs.
   * @throws GeneralSecurityException if a security error occurs.
   */
  public static void main(String[] args) throws IOException, GeneralSecurityException {
    Readable readable = new InputStreamReader(System.in);
    Appendable appendable = System.out;

    SheetsAndJava sheetsAndJava = new SheetsAndJava("1oQWexgC0J-Mni3Nz-B3PNRphiBeSKF9dhLUuGT5PaAk",
            "Pickachu");
    TextBasedView view = new TextBasedView(appendable);
    TextBasedController controller = new TextBasedController(sheetsAndJava, view, readable);
    controller.start();
  }
}

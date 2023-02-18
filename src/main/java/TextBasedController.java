public class TextBasedController {
  private final SheetsAndJava sheetsAndJava;
  private final TextBasedView view;
  private final Readable readable;

  public TextBasedController(SheetsAndJava sheetsAndJava, TextBasedView view, Readable readable) {
    this.sheetsAndJava = sheetsAndJava;
    this.view = view;
    this.readable = readable;
  }

  public void start() {
    SheetsAndJava.updateSpreadsheetID("");
  }
}

package text_based.controller;

import text_based.view.TextBasedView;

/**
 * The text_based.controller.TextBasedController class receives user input from the text_based.view.TextBasedView and updates the
 * text_based.controller.SheetsAndJava text_based.model accordingly.
 */
public class TextBasedController {
  private final SheetsAndJava sheetsAndJava;
  private final TextBasedView view;
  private final Readable readable;

  /**
   * Constructs a text_based.controller.TextBasedController object with the specified text_based.model, text_based.view, and input source.
   *
   * @param sheetsAndJava the text_based.controller.SheetsAndJava text_based.model to be updated
   * @param view          the text_based.view.TextBasedView to display data to the user
   * @param readable      the source of user input
   */
  public TextBasedController(SheetsAndJava sheetsAndJava, TextBasedView view, Readable readable) {
    this.sheetsAndJava = sheetsAndJava;
    this.view = view;
    this.readable = readable;
  }

  /**
   * This method starts the program and initializes the text_based.controller. It updates the spreadsheet ID
   * of the text_based.controller.SheetsAndJava text_based.model to an empty string.
   */
  public void start() {
    SheetsAndJava.updateSpreadsheetID("");
  }
}
package controller;

import view.TextBasedView;

/**
 * The controller.TextBasedController class receives user input from the view.TextBasedView and updates the
 * controller.SheetsAndJava model accordingly.
 */
public class TextBasedController {
  private final SheetsAndJava sheetsAndJava;
  private final TextBasedView view;
  private final Readable readable;

  /**
   * Constructs a controller.TextBasedController object with the specified model, view, and input source.
   *
   * @param sheetsAndJava the controller.SheetsAndJava model to be updated
   * @param view          the view.TextBasedView to display data to the user
   * @param readable      the source of user input
   */
  public TextBasedController(SheetsAndJava sheetsAndJava, TextBasedView view, Readable readable) {
    this.sheetsAndJava = sheetsAndJava;
    this.view = view;
    this.readable = readable;
  }

  /**
   * This method starts the program and initializes the controller. It updates the spreadsheet ID
   * of the controller.SheetsAndJava model to an empty string.
   */
  public void start() {
    SheetsAndJava.updateSpreadsheetID("");
  }
}
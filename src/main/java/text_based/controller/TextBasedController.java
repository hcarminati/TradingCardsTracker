package text_based.controller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Scanner;

import text_based.view.TextBasedView;

import static text_based.utilities.GoogleSheetsURLParser.extractSheetId;

/**
 * The text_based.controller.TextBasedController class receives user input from the text_based.view.TextBasedView and updates the
 * text_based.controller.SheetsAndJava text_based.model accordingly.
 */
public class TextBasedController {
  private final TextBasedView view;
  private final Readable readable;
  Properties props = new Properties();
  private SheetsAndJava sheetsAndJava;
  // Properties
  private String welcomeMessage;
  private String spreadsheetIdPrompt;
  private String invalidSpreadsheetIdMessage;
  private String successfullyConnectedMessage;
  private String exitMessage;
  private String dataAddedMessage;
  private String inputDataMessage;
  private String apiKeyMissingMessage;
  private String dataAddErrorMessage;
  private String permissionToAccessMessage;

  /**
   * Constructs a new TextBasedController object with the specified SheetsAndJava model,
   * TextBasedView view, and Readable source of user input. Loads properties from messages.properties
   * file and initializes the properties for this instance.
   *
   * @param sheetsAndJava The SheetsAndJava model to be updated.
   * @param view          The TextBasedView to display data to the user.
   * @param readable      The source of user input.
   * @throws IOException If there is an issue loading the properties file.
   */
  public TextBasedController(SheetsAndJava sheetsAndJava, TextBasedView view, Readable readable) throws IOException {
    this.sheetsAndJava = sheetsAndJava;
    this.view = view;
    this.readable = readable;

    loadProperties();
  }

  /**
   * Constructs a new TextBasedController object with the specified TextBasedView view and
   * Readable source of user input. Loads properties from messages.properties file and initializes
   * the properties for this instance.
   *
   * @param view     The TextBasedView to display data to the user.
   * @param readable The source of user input.
   * @throws IOException If there is an issue loading the properties file.
   */
  public TextBasedController(TextBasedView view, Readable readable) throws IOException {
    this.view = view;
    this.readable = readable;

    loadProperties();
  }

  /**
   * Loads properties from messages.properties file and initializes the properties for this instance.
   *
   * @throws IOException If there is an issue loading the properties file.
   */
  private void loadProperties() throws IOException {
    String filePath = System.getProperty("user.dir")
            + "/src/main/java/text_based/controller/resources/messages.properties";
    FileReader reader = new FileReader(filePath);
    props.load(reader);
    // Retrieve the messages using the keys
    welcomeMessage = props.getProperty("welcome.message");
    spreadsheetIdPrompt = props.getProperty("spreadsheet.id.prompt");
    invalidSpreadsheetIdMessage = props.getProperty("invalid.spreadsheet.id.message");
    successfullyConnectedMessage = props.getProperty("successfully.connected.message");
    exitMessage = props.getProperty("exit.message");
    dataAddedMessage = props.getProperty("data.added.message");
    inputDataMessage = props.getProperty("input.data.message");
    apiKeyMissingMessage = props.getProperty("api.key.missing.message");
    dataAddErrorMessage = props.getProperty("data.add.error.message");
    permissionToAccessMessage = props.getProperty("permission.to.access.message");
  }

  /**
   * This method starts the program and initializes the controller. It updates the spreadsheet ID
   * of the SheetsAndJava model to an empty string. The method continues to listen for input until
   * the user enters "exit".
   *
   * @throws IOException              If there is an issue with user input or the model.
   */
  public void start() throws IOException {
    Scanner scan = new Scanner(this.readable);

    this.view.renderMessage(welcomeMessage);

    SheetsAndJava sheetsAndJava = null;
    while (sheetsAndJava == null && scan.hasNext()) {
      this.view.renderMessage(spreadsheetIdPrompt);
      String url = extractSheetId(scan.next());
      try {
        sheetsAndJava = new SheetsAndJava(url);
        this.view.renderMessage(successfullyConnectedMessage + inputDataMessage);
      } catch (GoogleJsonResponseException e) {
        if (e.getStatusCode() == 403) {
          this.view.renderMessage(apiKeyMissingMessage + permissionToAccessMessage);
        } else {
          this.view.renderMessage(invalidSpreadsheetIdMessage);
        }
      } catch (Exception e) {
        this.view.renderMessage(invalidSpreadsheetIdMessage);
      }
    }

    while (scan.hasNextLine()) {
while (scan.hasNextLine()) {
      String input = scan.nextLine().trim();
      if (input.equals("\n")) {
        continue;
      }
      if (input.equals("exit")) {
        this.view.renderMessage(exitMessage);
        break;
      }
      if (input.length() > 0) {
        try {
          SheetsAndJava.writeData(input);
          this.view.renderMessage(dataAddedMessage + inputDataMessage);
        } catch (GoogleJsonResponseException e) {
          // Display error message to the user
          this.view.renderMessage(dataAddErrorMessage + permissionToAccessMessage);
        }
      }
    }
      }
    }
  }
}

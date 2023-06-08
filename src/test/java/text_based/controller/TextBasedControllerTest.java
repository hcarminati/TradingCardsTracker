package text_based.controller.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;

import text_based.controller.SheetsAndJava;
import text_based.controller.TextBasedController;
import text_based.view.TextBasedView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TextBasedControllerTest {

  private SheetsAndJava mockModel;

  @BeforeEach
  public void setUp() {
    mockModel = mock(SheetsAndJava.class);
  }

  @Test
  public void testWelcomeMessage() throws IOException, GeneralSecurityException {
    Readable readable = new StringReader("");
    Appendable appendable = new StringBuilder();
    TextBasedView mockView = new TextBasedView(appendable);

    TextBasedController controller = new TextBasedController(mockModel, mockView, readable);
    controller.start();
    assertEquals("Welcome to Trading Card Tracker. \n", appendable.toString());
  }

  @Test
  public void testStartMessage_InvalidSpreadsheetID() throws IOException, GeneralSecurityException {
    Readable readable = new StringReader("spreadsheet_id");
    Appendable appendable = new StringBuilder();
    TextBasedView mockView = new TextBasedView(appendable);

    TextBasedController controller = new TextBasedController(mockModel, mockView, readable);
    controller.start();
    assertEquals("Welcome to Trading Card Tracker. \n" +
            "Input your spreadsheet ID... \n" +
            "Invalid spreadsheet ID. Try again... \n", appendable.toString());
  }
}

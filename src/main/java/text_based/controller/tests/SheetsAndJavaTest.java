package text_based.controller.tests;

import org.junit.jupiter.api.Test;

import text_based.controller.SheetsAndJava;

import static org.testng.Assert.assertThrows;

public class SheetsAndJavaTest {

  @Test
  public void testInvalidSpreadsheetId() {
    assertThrows(Exception.class,
            () -> new SheetsAndJava("<invalid_spreadsheet_id>"));
  }

}

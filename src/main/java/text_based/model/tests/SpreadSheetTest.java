package text_based.model.tests;

import com.google.api.services.sheets.v4.Sheets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import text_based.model.SpreadSheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SpreadSheetTest {

  private static final String SPREADSHEET_ID = "spreadsheet_id";
  private SpreadSheet spreadSheet;
  private Sheets sheetsService;

  @BeforeEach
  public void setUp() {
    sheetsService = mock(Sheets.class);
    spreadSheet = new SpreadSheet(SPREADSHEET_ID, sheetsService);
  }

  @Test
  public void testGetSheetsService() {
    assertEquals(sheetsService, spreadSheet.getSheetsService());
  }

  @Test
  public void testGetSpreadsheetId() {
    assertEquals(SPREADSHEET_ID, spreadSheet.getSpreadsheetId());
  }
}

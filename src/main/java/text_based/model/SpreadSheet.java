package text_based.model;

import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * The SpreadSheet class provides access to a Google Sheets spreadsheet using the Google Sheets API.
 * It allows you to get the Sheets service instance and the spreadsheet ID for the spreadsheet you
 * want to work with.
 */
public class SpreadSheet {

  /**
   * The Sheets service instance used to access the spreadsheet.
   */
  private static Sheets sheetsService;

  /**
   * The ID of the spreadsheet being accessed.
   */
  private static String spreadsheetId;

  /**
   * Constructs a new SpreadSheet instance with the specified spreadsheet ID and Sheets service.
   *
   * @param spreadsheetId the ID of the spreadsheet to access
   * @param sheetsService the Sheets service instance to use for accessing the spreadsheet
   */
  public SpreadSheet(String spreadsheetId, Sheets sheetsService) {
    SpreadSheet.spreadsheetId = spreadsheetId;
    SpreadSheet.sheetsService = sheetsService;
  }

  /**
   * Gets the Sheets service instance used to access the spreadsheet.
   *
   * @return the Sheets service instance
   */
  public Sheets getSheetsService() {
    return sheetsService;
  }

  /**
   * Gets the ID of the spreadsheet being accessed.
   *
   * @return the spreadsheet ID
   */
  public String getSpreadsheetId() {
    return spreadsheetId;
  }
}

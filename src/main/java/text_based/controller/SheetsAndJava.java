package text_based.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import text_based.FetchData;
import text_based.model.SpreadSheet;

/**
 * The text_based.controller.SheetsAndJava class provides the functionality of accessing and modifying Google Sheets
 * using Google Sheets API. It includes methods for getting Sheets service instance, authorizing
 * credentials, adding data to the spreadsheet and other helpful methods.
 */
public class SheetsAndJava {
  private static final String APPLICATION_NAME = "Trading Card Tracker";
  private static SpreadSheet spreadsheet;

  public SheetsAndJava(String spreadsheetId) throws GeneralSecurityException, IOException {
    Sheets sheetsService = getSheetsService();

    // Check if the provided spreadsheet ID is valid
    try {
      sheetsService.spreadsheets().get(spreadsheetId).execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getStatusCode() == 404) {
        throw new IllegalArgumentException("Invalid spreadsheet ID");
      } else {
        throw e;
      }
    }

    spreadsheet = new SpreadSheet(spreadsheetId, sheetsService);
  }

  /**
   * Constructs a new SheetsAndJava instance with the specified spreadsheet ID and list of eBay
   * search terms.
   *
   * @param spreadsheetId the ID of the Google Sheets spreadsheet to write data to
   * @param searches      the eBay search terms to use for data retrieval
   * @throws GeneralSecurityException if there is a security-related error
   * @throws IOException              if an error occurs while communicating with the Google Sheets API or the eBay API
   */
  public SheetsAndJava(String spreadsheetId, String searches) throws GeneralSecurityException, IOException {
    spreadsheet = new SpreadSheet(spreadsheetId, getSheetsService());
    String[] searchesArray = searches.split("\n");

    for (String s : searchesArray) {
      writeData(s);
    }
  }

  /**
   * Creates an instance of authorized Credential object.
   *
   * @return authorized Credential object
   * @throws IOException              if failed to read a file or data
   * @throws GeneralSecurityException if authorization is not successful
   */
  private static Credential authorize() throws IOException, GeneralSecurityException {
    InputStream in = SheetsAndJava.class.getResourceAsStream("/credentials.json");
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
            GsonFactory.getDefaultInstance(), new InputStreamReader(in)
    );

    List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
            clientSecrets, scopes)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("token")))
            .setAccessType("offline")
            .build();

    Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver())
            .authorize("user");

    return credential;
  }

  /**
   * Gets the Sheets service instance using authorized Credential object.
   *
   * @return Sheets service instance
   * @throws IOException              if failed to read a file or data
   * @throws GeneralSecurityException if authorization is not successful
   */
  public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
    Credential credential = authorize();
    return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(), credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
  }

  /**
   * Adds the data to the spreadsheet. It checks if the card has been previously added.
   * If the card has been previously added, it will calculate the quantity and updates it.
   * If the card has not been added, it will be added to the spreadsheet in the next empty row.
   *
   * @param data the name to look for or add
   * @throws IOException              if failed to read a file or data
   */
public static void writeData(String data) throws IOException {
    String range = "B3";

    int quantity = 1;
    int duplicateCellNum = -1;
    long timestamp = System.currentTimeMillis();

    // perhaps make it one method that checks if it is duplicate and calculates the quantity
    // this may reduce the running time
    if (duplicate(data)) {
      quantity = quantity(data);
      duplicateCellNum = getDuplicateCellNum(data);
      setQuantity(quantity, duplicateCellNum);
    }

    // If not duplicate, add it.
    else {
      // Check if the timestamp is more than 24 hours ago
      if (timestamp - getTimestamp(data) > 24 * 60 * 60 * 1000) {
        ValueRange appendName = new ValueRange().setValues(
                Arrays.asList(Arrays.asList(quantity, data, FetchData.search(data), FetchData.getURL(data), timestamp)));

        // Is this used? - because it is grey
        AppendValuesResponse appendResults = spreadsheet.getSheetsService().spreadsheets().values()
                .append(spreadsheet.getSpreadsheetId(), range, appendName)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
      } else {
        // Use the existing price and URL
        ValueRange appendName = new ValueRange().setValues(
                Arrays.asList(Arrays.asList(quantity, data, getPrice(data), getURL(data), timestamp)));

        // Is this used? - because it is grey
        AppendValuesResponse appendResults = spreadsheet.getSheetsService().spreadsheets().values()
                .append(spreadsheet.getSpreadsheetId(), range, appendName)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
      }

      CellData setUserEnteredValue = new CellData()
              .setUserEnteredValue(new ExtendedValue().setStringValue("example text"));

      CellFormat cellFormat = new CellFormat();

      // Don't think I need this
      // cellFormat.setTextFormat(new TextFormat().setBold(true));

      setUserEnteredValue.setUserEnteredFormat(cellFormat);

      notBold();
    }

  }
    }

  }

  /**
   * Sets the quantity for a given cell in the spreadsheet, given the quantity and the cell
   * number of the card.
   *
   * @param quantity the new quantity to be set
   * @param cellNum  the cell number to set the quantity for
   * @throws IOException              if an error occurs while accessing the Google Sheets API
   */
  public static void setQuantity(int quantity, int cellNum) throws IOException {
    String range = "Sheet1!B" + cellNum;
    String data = "";
    ValueRange response = spreadsheet.getSheetsService().spreadsheets().values()
            .get(spreadsheet.getSpreadsheetId(), range)
            .execute();

    List<List<Object>> values = response.getValues();
    String currentQuantityString = values.get(0).get(0).toString();
    int currentQuantity = Integer.valueOf(currentQuantityString);

    if (values.isEmpty()) {
      System.out.println("No data found.");
    } else {
      for (List row : values) {
        data += row.get(0);

        ValueRange requestBody = new ValueRange();
        requestBody.setValues(
                Arrays.asList(
                        Arrays.asList(quantity + currentQuantity - 1)));


        UpdateValuesResponse updateResults = spreadsheet.getSheetsService().spreadsheets().values()
                .update(spreadsheet.getSpreadsheetId(), range, requestBody)
                .setValueInputOption("USER_ENTERED")
//            .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
      }
    }
  }

  /**
   * Returns the number of the first duplicate cell in the specified row, or -1 if there are no
   * duplicates. A duplicate cell is one that had the same value as another cell in the same column.
   * In this case, the cell with the same value in the "Name" column.
   *
   * @param cell the value of the cell to check for duplicates
   * @return the number of the first duplicate cell in the range, or -1 if there are no duplicates
   * @throws IOException if there is an error retrieving the values from the specified range
   */
  public static int getDuplicateCellNum(String cell) throws IOException {
    String range = "C3:C1000";
    int currentCell = 3;

    ValueRange response = spreadsheet.getSheetsService().spreadsheets().values()
            .get(spreadsheet.getSpreadsheetId(), range)
            .execute();

    List<List<Object>> values = response.getValues();

    for (List<Object> listObject : values) {
      if (!listObject.get(0).equals(cell)) {
        currentCell++;
      }
      if (listObject.get(0).equals(cell)) {
        return currentCell;
      }
    }

    return -1;
  }

  /**
   * Removes bold formatting from the test in a specified range in the sheet. The method sends a
   * batch update request to the Google Sheets API to set the text format of the cells to not bold.
   * This is usually ran when new information is added or when a cell is updated.
   *
   * @throws IOException if an error occurs while communicating with the Google Sheets API
   */
  public static void notBold() throws IOException {
    RepeatCellRequest repeatCellRequest = new RepeatCellRequest();
    CellData cellData = new CellData();

    CellFormat cellFormat = new CellFormat();
    TextFormat textFormat = new TextFormat();
    textFormat.setBold(false);

    cellFormat.setTextFormat(textFormat);
    cellData.setUserEnteredFormat(cellFormat);
    repeatCellRequest.setCell(cellData);
    repeatCellRequest.setFields("userEnteredFormat(textFormat)");

    GridRange gridRange = new GridRange();
    gridRange
            .setSheetId(0) // Number this sheet is
            .setStartRowIndex(2);
    repeatCellRequest.setRange(gridRange);

    List<Request> requests = new ArrayList<>();
    requests.add(new Request().setRepeatCell(repeatCellRequest));

    BatchUpdateSpreadsheetRequest body2 = new BatchUpdateSpreadsheetRequest()
            .setRequests(requests);

    spreadsheet.getSheetsService().spreadsheets()
            .batchUpdate(spreadsheet.getSpreadsheetId(), body2)
            .execute();
  }

  /**
   * Checks if the specified data (trading card) already exists in the sheet.
   *
   * @param data the name to be checked for duplicates
   * @return true if the specified data already exists in the sheet, false otherwise
   * @throws IOException if an error occurs while accessing the Google Sheets API
   */
  public static boolean duplicate(String data) throws IOException {
    String range = "C3:C5";

    ValueRange response = spreadsheet.getSheetsService().spreadsheets().values()
            .get(spreadsheet.getSpreadsheetId(), range)
            .execute();

    List<List<Object>> values = response.getValues();

    if (values != null) {
      for (List<Object> listObject : values) {
        if (listObject.get(0).equals(data)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Returns the number of times the specified data appears in the range "C3:C5" of the spreadsheet.
   * If the data does not appear at least once, it will return 1 because the specified card has not
   * been added yet to the sheet.
   *
   * @param data the card to be searched for in the spreadsheet
   * @return the number of times the given data appears in the cell range, or 1 if the data searched
   * for does not appear
   * @throws IOException if an error occurs while accessing the spreadsheet
   */
  public static int quantity(String data) throws IOException {
    String range = "C3:C5";
    int quantity = 1;

    ValueRange response = spreadsheet.getSheetsService().spreadsheets().values()
            .get(spreadsheet.getSpreadsheetId(), range)
            .execute();

    List<List<Object>> values = response.getValues();

    for (List<Object> listObject : values) {
      // if cell is empty an error is thrown here
      if (listObject.get(0).equals(data)) {
        quantity++;
      }
    }

    return quantity;
  }

  /**
   * Returns the data in the specified cell.
   *
   * @param cell the cell to read data from, in A1 notation
   * @return the data in the specified cell as a string, or an empty string if the cell is empty or
   * does not exist
   * @throws IOException if an error occurs while communicating with the Google Sheets API
   */
  public static String readData(String cell) throws IOException {
    String range = cell + ":" + cell;
    String data = "";
    ValueRange response = spreadsheet.getSheetsService().spreadsheets().values()
            .get(spreadsheet.getSpreadsheetId(), range)
            .execute();

    List<List<Object>> values = response.getValues();

    if (values == null || values.isEmpty()) {
      System.out.println("No data found.");
    } else {
      for (List row : values) {
        data += row.get(0);
      }
    }

    return data;
  }
}

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
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
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SheetsAndJava {
  private static Sheets sheetsService;
  private static String APPLICATION_NAME = "Google Sheets Example";
//  private static String SPREADSHEET_ID = "14vpCmaMtFc4bWGMa5WmT_-JMTiy1-nPg1esIlW3noBU";
 // https://docs.google.com/spreadsheets/d/1oQWexgC0J-Mni3Nz-B3PNRphiBeSKF9dhLUuGT5PaAk/edit?usp=sharing
  private static String SPREADSHEET_ID;

  private static final FetchData fetchData = new FetchData();

  public SheetsAndJava() throws GeneralSecurityException, IOException {
    sheetsService = getSheetsService();
    SPREADSHEET_ID = "1oQWexgC0J-Mni3Nz-B3PNRphiBeSKF9dhLUuGT5PaAk";
  }

  /**
   * MAY NOT NEED LATER, SO CAN DELETE
   * This constructor is used to run the program by initially putting in cards to search.
   * @param searches
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public SheetsAndJava(String searches) throws GeneralSecurityException, IOException {
    SPREADSHEET_ID = "1oQWexgC0J-Mni3Nz-B3PNRphiBeSKF9dhLUuGT5PaAk";
    sheetsService = getSheetsService();
    String[] searchesArray = searches.split("\n");

    for (String s : searchesArray) {
      writeData(s);
    }
  }
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
//    flow, new LocalServerReceiver.Builder().setPort(8888).build())
//            .authorize("user");

    return credential;
  }

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
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static void writeData(String data) throws IOException, GeneralSecurityException {
    String range = "B3";

    int quantity = 1;
    int duplicateCellNum = -1;

    // perhaps make it one method that checks if it is duplicate and calculates the quantity
    // this may reduce the running time
    if (duplicate(data)) {
      quantity = quantity(data);
      duplicateCellNum = getDuplicateCellNum(data);
      setQuantity(quantity, duplicateCellNum);
    }

    // If not duplicate, add it.
    else {
      ValueRange appendName = new ValueRange().setValues(
              Arrays.asList(Arrays.asList(quantity, data, fetchData.search(data), fetchData.getURL(data))));

      // Is this used? - because it is grey
      AppendValuesResponse appendResults = sheetsService.spreadsheets().values()
              .append(SPREADSHEET_ID, range, appendName)
              .setValueInputOption("USER_ENTERED")
              .setInsertDataOption("INSERT_ROWS")
              .setIncludeValuesInResponse(true)
              .execute();

      CellData setUserEnteredValue = new CellData()
              .setUserEnteredValue(new ExtendedValue().setStringValue("example text"));

      CellFormat cellFormat = new CellFormat();

      // Don't think I need this
      // cellFormat.setTextFormat(new TextFormat().setBold(true));

      setUserEnteredValue.setUserEnteredFormat(cellFormat);

      notBold();
    }

  }

  //(?)
  /**
   * Sets the quantity of the cell that keeps track of the quantity of a certain card.
   *
   * @param quantity the current quantity before adding one
   * @param duplicateCellNum the
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static void setQuantity(int quantity, int duplicateCellNum) throws IOException, GeneralSecurityException {
    String range = "Sheet1!B" + duplicateCellNum;
    String data = "";
    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();
    String currentQuantityString = values.get(0).get(0).toString();
    int currentQuantity = Integer.valueOf(currentQuantityString);

    if (values == null || values.isEmpty()) {
      System.out.println("No data found.");
    } else {
      for (List row : values) {
        data += row.get(0);

        ValueRange requestBody = new ValueRange();
        requestBody.setValues(
                Arrays.asList(
                        Arrays.asList(quantity + currentQuantity - 1)));


        UpdateValuesResponse updateResults = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, range, requestBody)
                .setValueInputOption("USER_ENTERED")
//            .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
      }
    }
  }

  /**
   * Goes through the cells with the trading card names and checks if there exists a cell with
   * the given name and returning its cell number.
   *
   * @param cell the string to be looked for
   * @return the location of the first instance of the cell with the given data
   */
  public static int getDuplicateCellNum(String cell) throws IOException {
    String range = "C3:C1000";
    int currentCell = 3;

    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    for(int i = 0; i < values.size(); i++) {
      if (!values.get(i).get(0).equals(cell)) {
        currentCell++;
      }
      if (values.get(i).get(0).equals(cell)) {
        return currentCell;
      }
    }

    return -1;
  }

  /**
   * Sets the cells to not be bold when new information is added or when a cell is updated.
   *
   * @throws IOException
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

    sheetsService.spreadsheets()
            .batchUpdate(SPREADSHEET_ID, body2)
            .execute();
  }

  /**
   * Goes through the cells with the names of the trading cards and checks if the inputted data
   * has already been added to the Google Sheet.
   *
   * @param data the name being looked for
   * @return false if there is no duplicate, true otherwise
   * @throws IOException
   */
  public static boolean duplicate(String data) throws IOException {
    String range = "C3:C5";

    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    if (values != null) {
      for (int i = 0; i < values.size(); i++) {
        if (values.get(i).get(0).equals(data)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Checks if the inputted data has already been added. If it has, it returns the total, quantity
   * of the inputted data. If it has not, it will simply return 1 because it is being added for
   * the first time.
   *
   * @param data the name being looked for
   * @return the number of times the given data appears in the cell range
   * @throws IOException
   */
  public static int quantity(String data) throws IOException {
    String range = "C3:C5";
    int quantity = 1;

    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    for(int i = 0; i < values.size(); i++) {

      // if cell is empty an error is thrown here
      if (values.get(i).get(0).equals(data)) {
        quantity ++;
      }
    }

    return quantity ;
  }

  public static String readData(String cell) throws IOException {
    String range = cell + ":" + cell;
    String data = "";
    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    if (values == null || values.isEmpty()) {
      System.out.println("No data found.");
    }
    else {
      for (List row : values) {
        data += row.get(0);
      }
    }

    return data;
  }

  public static void updateSpreadsheetID(String id) {
    SPREADSHEET_ID = id;
  }
}

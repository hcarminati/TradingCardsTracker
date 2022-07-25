import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
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


public class SheetsAndJava {
  private static Sheets sheetsService;
  private static String APPLICATION_NAME = "Google Sheets Example";
  private static String SPREADSHEET_ID = "15OaVdDtVqKA4e9qGpLgbBUO-TXbemXwEJ2t3V8Viy5w";

  private static final FetchData fetchData = new FetchData();
  public SheetsAndJava(String searches) throws GeneralSecurityException, IOException {
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

  public static void writeData(String data) throws IOException {
    String range = "B3";

    int quantity = 1;
    int duplicateCellNum = -1;

    if (duplicate(data)) {
      quantity = quantity(data);
      duplicateCellNum = getDuplicateCellNum(data);
      setQuantity(quantity, duplicateCellNum);
    }

    ValueRange appendName = new ValueRange().setValues(
            Arrays.asList(Arrays.asList(quantity, data, fetchData.search(data), fetchData.getURL(data))));

    AppendValuesResponse appendResults = sheetsService.spreadsheets().values()
            .append(SPREADSHEET_ID, range, appendName)
            .setValueInputOption("USER_ENTERED")
            .setInsertDataOption("INSERT_ROWS")
            .setIncludeValuesInResponse(true)
            .execute();

    CellData setUserEnteredValue = new CellData()
            .setUserEnteredValue(new ExtendedValue().setStringValue("example text"));

    CellFormat cellFormat = new CellFormat();
    cellFormat.setTextFormat(new TextFormat().setBold(true));

    setUserEnteredValue.setUserEnteredFormat(cellFormat);

    notBold();
  }

  public static void setQuantity(int quantity, int duplicateCellNum) throws IOException {
    ValueRange appendName = new ValueRange().setValues(
            Arrays.asList(Arrays.asList(quantity)));

    String range = "A" + 3 + ":" + "A" + 3;

//    AppendValuesResponse appendResults = sheetsService.spreadsheets().values()
//            .append(SPREADSHEET_ID, range , appendName)
//            .setValueInputOption("USER_ENTERED")
//            .setInsertDataOption("INSERT_ROWS")
//            .setIncludeValuesInResponse(true)
//            .execute();
  }

  /**
   *
   * @param cell
   * @return the first instance of the cell with the given data (String)
   */
  public static int getDuplicateCellNum(String cell) throws IOException {
    String range = "B3:B1000";
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
            .setSheetId(0) // the sheet this range is on
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
   *
   * @param data
   * @return false if there is no duplicate, true otherwise
   * @throws IOException
   */
  public static boolean duplicate(String data) throws IOException {
    String range = "B3:B1000";

    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    for(int i = 0; i < values.size(); i++) {
      if (values.get(i).get(0).equals(data)) {
        return true;
      }
    }

    return false;
  }

  public static int quantity(String data) throws IOException {
    String range = "B3:B1000";
    int quantity = 1;

    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    for(int i = 0; i < values.size(); i++) {
      if (values.get(i).get(0).equals(data)) {
        quantity ++;
      }
    }

    return quantity;
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
//        System.out.println(row.get(0));
      }
    }

    return data;
  }

//  public static void main(String[] args) throws IOException, GeneralSecurityException {
//    sheetsService = getSheetsService();
//    writeData("spiderman");
//  }
}

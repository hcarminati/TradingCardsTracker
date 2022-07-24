import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SheetsAndJava {
  private static Sheets sheetsService;
  private static String APPLICATION_NAME = "Google Sheets Example";
  private static String SPREADSHEET_ID = "15OaVdDtVqKA4e9qGpLgbBUO-TXbemXwEJ2t3V8Viy5w";

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

  public static void readData() throws IOException {
    String range = "A2:E8";

    ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, range)
            .execute();

    List<List<Object>> values = response.getValues();

    if (values == null || values.isEmpty()) {
      System.out.println("No data found.");
    }
    else {
      for (List row : values) {
        System.out.println(row.get(0));
      }
    }
  }

  public static void writeData() throws IOException {
    String range = "A10:C10";
    // If you keep the same range and there is already something in those
    // cells than it just adds it to the next empty cells.
    // *Find out a way to check if it is not empty. If it is empty, then you add it.
    ValueRange appendBody = new ValueRange()
            .setValues(Arrays.asList(
                    Arrays.asList(new FetchData().getPrice(
                            "https://www.ebay.com/itm/115465469421?_trkparms=amclksrc%3DITM%26aid%3D1110006%26algo%3DHOMESPLICE.SIM%26ao%3D1%26asc%3D20200818143230%26meid%3D894b1840e2194bcc8547048035c74066%26pid%3D101224%26rk%3D5%26rkt%3D5%26sd%3D385009427122%26itm%3D115465469421%26pmt%3D1%26noa%3D1%26pg%3D2047675%26algv%3DDefaultOrganicWeb%26brand%3DWizards+of+the+Coast&_trksid=p2047675.c101224.m-1"
                    ), "is ", "funny")
            ));

    AppendValuesResponse appendResults = sheetsService.spreadsheets().values()
            .append(SPREADSHEET_ID, range, appendBody)
            .setValueInputOption("USER_ENTERED")
            .setInsertDataOption("INSERT_ROWS")
            .setIncludeValuesInResponse(true)
            .execute();
  }

  public static void main(String[] args) throws IOException, GeneralSecurityException {
    sheetsService = getSheetsService();
    writeData();
  }
}

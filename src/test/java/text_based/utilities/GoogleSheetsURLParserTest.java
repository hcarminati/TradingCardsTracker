package text_based.utilities;

import org.junit.jupiter.api.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class GoogleSheetsURLParserTest {
  @Test
  public void testExtractSheetIdSuccessful() {
    String httpsUrl = "https://docs.google.com/spreadsheets" +
            "/d/1vR5QMaAW5LOUo2LwV50loukHbXcXh5YtCHSschmcHGU/edit#gid=0";
    String wwwUrl = "www.docs.google.com/spreadsheets" +
            "/d/1vR5QMaAW5LOUo2LwV50loukHbXcXh5YtCHSschmcHGU/edit#gid=0";

    assertEquals("1vR5QMaAW5LOUo2LwV50loukHbXcXh5YtCHSschmcHGU",
            GoogleSheetsURLParser.extractSheetId(httpsUrl));
    assertEquals("1vR5QMaAW5LOUo2LwV50loukHbXcXh5YtCHSschmcHGU",
            GoogleSheetsURLParser.extractSheetId(wwwUrl));
  }

  @Test
  public void testExtractSheetIdUnSuccessful() {
    String noDUrl = "https://docs.google.com/spreadsheets" +
            "1vR5QMaAW5LOUo2LwV50loukHbXcXh5YtCHSschmcHGU/edit#gid=0";
    String emptyUrl = "";

    assertNull(GoogleSheetsURLParser.extractSheetId(noDUrl));
    assertNull(GoogleSheetsURLParser.extractSheetId(emptyUrl));
  }
}

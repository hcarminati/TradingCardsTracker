package text_based.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleSheetsURLParser {

  public static String extractSheetId(String url) {
    String pattern = "/d/([a-zA-Z0-9-_]+)";
    Pattern regexPattern = Pattern.compile(pattern);
    Matcher matcher = regexPattern.matcher(url);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }
}

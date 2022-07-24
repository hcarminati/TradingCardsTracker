import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class FetchData {
  public static String getPrice(String url) throws IOException {
    Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/17.0").get();

    Elements temp = doc.select("div.val");
    Element webData = temp.get(0);

    return webData.getElementsByTag("span").first().text();
  }

  public static String search(String search) throws IOException {
    Document doc = Jsoup.connect("https://www.ebay.com")
            .data("_nkw", search)
//            .data("sort", "asc")
//            .data("category", "All Categories")
           .post();

    Elements temp = doc.select("div.val");
    Element webData = temp.get(0);
    return webData.getElementsByTag("span").first().text();
  }

  public static void main(String[] args) throws IOException {
    System.out.println("The price is " + search("HITMONCHAN 24/115"));
  }
}

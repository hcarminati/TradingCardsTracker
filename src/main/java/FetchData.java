import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class FetchData {
  public static String getPrice(String url) throws IOException {
    Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/17.0").get();

    Elements temp = doc.select("div.val");
    Element webData = temp.get(0);

    return webData.getElementsByTag("span").first().text();
  }

  public static String search(String search) throws IOException {
    String url = getURL(search);

    Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/17.0").get();
    Elements temp = doc.select("div.srp-river-results");
    Element webData = temp.get(0);

    return webData.getElementsByClass("s-item__price").first().text();
  }

  public static String getURL(String search) throws IOException {
    String url = "https://www.ebay.com/sch/i.html?_nkw=" + search
            + "&LH_Sold=1&LH_Complete=1";
    return url;
  }

  public static void main(String[] args) throws IOException {
    System.out.println("The price is " + search("spiderman"));
  }
}

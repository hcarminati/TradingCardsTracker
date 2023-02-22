package text_based;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * This class is used to extract data stored in HTML documents from the eBay website.
 * It uses the Jsoup open source Java library to parse and manipulate the HTML data.
 */
public class FetchData {
  /**
   * Retrieves the price of the latest sold item based on the provided URL.
   *
   * @param url the URL of the item's eBay page
   * @return the price of the item
   * @throws IOException if the HTML element with the price is not found
   */
  public static String getPrice(String url) throws IOException {
    Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/17.0").get();

    Elements temp = doc.select("div.val");
    Element webData = temp.get(0);

    return webData.getElementsByTag("span").first().text();
  }

  /**
   * Retrieves the price of the latest sold item with the given search term.
   *
   * @param search the search term for the item
   * @return the price of the item
   * @throws IOException if the HTML element with the price is not found
   */
  public static String search(String search) throws IOException {
    String url = getURL(search);

    Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/17.0").get();
    Elements temp = doc.select("div.srp-river-results");
    Element webData = temp.get(0);

    return webData.getElementsByClass("s-item__price").first().text();
  }

  /**
   * Generates the eBay URL for the latest sold items with the given search term.
   *
   * @param search search the search term for the item
   * @return the eBay URL for the search term
   */
  public static String getURL(String search) {
    String url = "https://www.ebay.com/sch/i.html?_nkw=" + search
            + "&LH_Sold=1&LH_Complete=1";
    return url;
  }

  //TODO: Delete this after tests are written
  public static void main(String[] args) throws IOException {
    System.out.println("The price is " + search("spiderman"));
  }
}

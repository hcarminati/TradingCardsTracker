package text_based.model;

/**
 * The Card class represents a trading card with its attributes.
 */
public class Card {
  private static String name;
  private static String type;
  private static String rarity;
  private static int quantity;
  private static int recentlySoldPrice;

  /**
   * Retrieves the name of the card.
   *
   * @return The name of the card.
   */
  public static String getName() {
    return name;
  }

  /**
   * Retrieves the type of the card.
   *
   * @return The type of the card.
   */
  public static String getType() {
    return type;
  }

  /**
   * Retrieves the rarity of the card.
   *
   * @return The rarity of the card.
   */
  public static String getRarity() {
    return rarity;
  }

  /**
   * Retrieves the quantity of the card.
   *
   * @return The quantity of the card.
   */
  public static int getQuantity() {
    return quantity;
  }

  /**
   * Retrieves the recently sold price of the card.
   *
   * @return The recently sold price of the card.
   */
  public static int getRecentlySoldPrice() {
    return recentlySoldPrice;
  }
}

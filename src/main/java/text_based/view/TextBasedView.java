package view;

import java.io.IOException;

/**
 * This class is responsible for rendering output on the command line.
 */
public class TextBasedView {
  protected final Appendable destination;

  /**
   * Constructor for view.TextBasedView class that sets the destination for output.
   *
   * @param destination the output destination, which is usually System.out
   */
  public TextBasedView(Appendable destination) {
    this.destination = destination;
  }

  /**
   * Renders the given message to the destination.
   *
   * @param message the message to be rendered
   * @throws IOException if there is an issue writing to the output destination
   */
  public void renderMessage(String message) throws IOException {
    destination.append(message);
  }
}

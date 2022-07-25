import java.io.IOException;

public class TextBasedView {
  protected final Appendable destination;

  public TextBasedView(Appendable destination) {
    this.destination = destination;
  }

  public void renderMessage(String message) throws IOException {
    destination.append(message);
  }
}

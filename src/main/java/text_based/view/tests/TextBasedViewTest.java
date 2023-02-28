package text_based.view.tests;

import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;

import text_based.view.TextBasedView;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class TextBasedViewTest {
  @Test
  public void testRenderMessage() throws IOException {
    StringWriter writer = new StringWriter();
    TextBasedView view = new TextBasedView(writer);

    view.renderMessage("Hello, world!");

    assertEquals("Hello, world!", writer.toString());
  }

  @Test
  public void testRenderMultipleMessages() throws IOException {
    StringWriter writer = new StringWriter();
    TextBasedView view = new TextBasedView(writer);

    view.renderMessage("Hello, ");
    view.renderMessage("world!");

    assertEquals("Hello, world!", writer.toString());
  }

  @Test
  public void testRenderEmptyMessage() throws IOException {
    StringWriter writer = new StringWriter();
    TextBasedView view = new TextBasedView(writer);

    view.renderMessage("");

    assertEquals("", writer.toString());
  }

  @Test
  public void testRenderMessageWithIOException() {
    Appendable brokenAppendable = new Appendable() {
      @Override
      public Appendable append(CharSequence csq) throws IOException {
        throw new IOException("Broken!");
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException("Broken!");
      }

      @Override
      public Appendable append(char c) throws IOException {
        throw new IOException("Broken!");
      }
    };

    TextBasedView view = new TextBasedView(brokenAppendable);

    assertThrows(IOException.class, () -> view.renderMessage("This should fail"));
  }
}

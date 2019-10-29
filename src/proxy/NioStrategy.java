package proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class NioStrategy implements InputStrategy {
  private final List<String> lines;
  private int line = 0;
  private final int size;

  // Abstraction function:
  // AF(lines): An input instance reading from given path using Nio
  // Representation invariant:
  // line <= size
  // Safety from rep exposure:
  // all fields are private and final, lines will only be produced once
  
  /*
   * Checking rep invariant.
   */
  public void checkrep() {
    assert line <= size;
  }

  /**
   * Read the whole file from given source.
   * 
   * @param file the given path of input source
   * @throws FileNotFoundException when couldn't file such file or occurs any wrong
   */
  public NioStrategy(File file) throws FileNotFoundException {
    try {
      Path path = Paths.get(file.toString());
      lines = Files.readAllLines(path, Charset.forName("utf-8"));
      size = lines.size();
    } catch (InvalidPathException e) {
      throw new FileNotFoundException();
    } catch (IOException e) {
      throw new FileNotFoundException();
    }
  }

  @Override
  public String nextLine() {
    if (line == size) {
      return null;
    }
    line++;
    return lines.get(line - 1);
  }
}

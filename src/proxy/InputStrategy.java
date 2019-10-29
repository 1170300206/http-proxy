package proxy;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * I/O factory using to return a specific way of I/O solutions. Actually when constructing the
 * instance, it has already read all lines and saved.
 */
public interface InputStrategy {

  /**
   * Return an input reader instance.
   * 
   * @return an input reader instance
   */
  public static InputStrategy input(File file) throws FileNotFoundException {
    return new NioStrategy(file);
  }

  /**
   * Return the nextLine From input.
   * 
   * @return the String format next line, contains all space exclude '\n' or '\r\n', null if there
   *         is no next line or error occurs
   */
  public String nextLine();
}

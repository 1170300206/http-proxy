package proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * An output instance using for output to files.
 */
public interface OutputStrategy {

  /**
   * Return an output instance.
   * 
   * @param file the file path
   * @param append if it can append file
   * @return OutputStrategy the output instance
   * @throws FileNotFoundException if there is no such file
   */
  public static OutputStrategy emptyInstance(File file, boolean append) throws FileNotFoundException {
    return new BufferedStreamStrategy(file, append);
  }

  /**
   * Output files to specific files.
   * 
   * @param string String info that are to be output
   */
  public void outPut(String string) throws IOException;

  /**
   * Close the printer, if it has been closed, it does nothing.
   */
  public void close() throws IOException;
}

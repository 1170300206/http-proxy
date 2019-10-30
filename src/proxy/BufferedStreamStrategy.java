package proxy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BufferedStreamStrategy implements OutputStrategy {
  private final BufferedOutputStream bf;
  private final FileOutputStream fo;
  // Abstraction function:
  // AF(bf, fo): An output instance related to specific files, fo is the file output stream.
  // Safety from rep exposure:
  // all fields are private and final, bf and fo will only be produced once.

  public BufferedStreamStrategy(File file, boolean append) throws FileNotFoundException {
    fo = new FileOutputStream(file, append);
    bf = new BufferedOutputStream(fo);
  }

  @Override
  public void outPut(String string) throws IOException {
    bf.write(string.getBytes());
  }

  @Override
  public void close() throws IOException {
    bf.close();
    fo.close();
  }

}

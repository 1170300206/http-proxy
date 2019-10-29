package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.util.Length;

/**
 * A response class, which contains the whole response body
 * 
 */
public class Response {
  private List<byte[]> responses = new ArrayList<>();
  private final int MAXLENGTH = 1024;


  /**
   * get response and write directly
   * @param in input stream of server
   * @param out_c output stream of the client 
   * @throws IOException if write error occurs 
   */
  public Response(InputStream in, OutputStream out_c) throws IOException {
    // current input that did not being buffered
    byte[] response = new byte[MAXLENGTH];
    int len = -1;
    System.out.println("Start to fetch response");
    while ((len = in.read(response)) != -1) {
      if(len == MAXLENGTH) {
      responses.add(response);
      out_c.write(response);
      }else {
        byte[] tmp = new byte[len];
        System.arraycopy(response, 0, tmp, 0, len);
        // write directly before finished
        out_c.write(tmp);
        responses.add(tmp);
      }
      response = new byte[MAXLENGTH];
      /*
      if(check(response, len)) {
        break;
      }*/
    }
    System.out.println("finished fetch");
  }

  /**
   * Check if it is EOF contained
   * 
   * @param bytes byte stream
   * @return true if the byte stream contains EOF, false if not
   */
  private boolean check(byte[] bytes, int length) {
    // bytes should contains 13 10 13 10 as the end.
    if (length < 4) {
      return false;
    }
    if ((int) bytes[length - 1] != 10 || (int) bytes[length - 2] != 13) {
      return false;
    }
    if ((int) bytes[length - 3] != 10 || (int) bytes[length - 4] != 13) {
      return false;
    }
    return true;
  }

  /**
   * Return the whole response.
   * 
   * @return the whole response.
   */
  public List<byte[]> getResponse() {
    System.out.println("return a response");
    return new ArrayList<byte[]>(responses);
  }
}

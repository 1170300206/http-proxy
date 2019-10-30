package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A response class, which contains the whole response body
 * 
 */
public class Response {
  private List<byte[]> responses = new ArrayList<>();
  private final int MAXLENGTH = 1024;
  private int state;
  private Date date;

  /**
   * get response and write directly
   * 
   * @param in input stream of server
   * @param out_c output stream of the client
   * @param cacher manage the cache
   * @param getDate true if the cacher is on
   * @throws IOException if write error occurs
   * @throws ParseException
   */
  public Response(InputStream in, OutputStream out_c, boolean getDate)
      throws IOException, ParseException {
    // current input that did not being buffered
    byte[] response = new byte[MAXLENGTH];
    int len = -1;
    System.out.println("Start to fetch response");
    while ((len = in.read(response)) != -1) {
      if (getDate) {
        String tmp = new String(response);
        // get first line and check state
        int firstEnter = tmp.indexOf("\r\n");
        String firstLine = tmp.substring(0, firstEnter);
        state = Integer.valueOf(firstLine.split(" ")[1]);
        int dateIndex = tmp.indexOf("Date");
        int dateEnd = tmp.substring(dateIndex).indexOf("\r\n");
        System.out.println(dateIndex + dateEnd);
        // get the line with date
        String dateLine = tmp.substring(dateIndex, dateEnd);
        Pattern pattern = Pattern.compile("Date: (.*) GMT");
        Matcher matcher = pattern.matcher(dateLine);
        if (!matcher.matches()) {
          throw new IOException();
        }
        dateLine = matcher.group(1);
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HHHH:mm:ss ");
        date = dateFormat.parse(dateLine);
        getDate = false;
      }
      if (len == MAXLENGTH) {
        responses.add(response);
        out_c.write(response);
      } else {
        byte[] tmp = new byte[len];
        System.arraycopy(response, 0, tmp, 0, len);
        // write directly before finished
        out_c.write(tmp);
        responses.add(tmp);
      }
      response = new byte[MAXLENGTH];
      /*
       * if(check(response, len)) { break; }
       */
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

  /**
   * Return the http state.
   * 
   * @return the http state.
   */
  public int state() {
    return state;
  }

  /**
   * return the date of current website
   * 
   * @return the date of current website
   */
  public Date date() {
    return date;
  }
}

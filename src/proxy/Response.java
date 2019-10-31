package proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A response class, which contains the whole response body
 * 
 */
public class Response {
  private List<byte[]> responses = new ArrayList<>();
  private final int MAXLENGTH = 2048;
  private int state;
  private Date date;
  private DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

  /**
   * get response and write directly
   * 
   * @param in input stream of server
   * @param out_c output stream of the client
   * @param cacher manage the cache
   * @param cacher cache manager
   * @param url link of the web site that being requested
   * @throws IOException if write error occurs
   * @throws ParseException
   */
  public Response(InputStream in, OutputStream out_c, String url, Cacher cacher)
      throws IOException, ParseException {
    try {
      // current input that did not being buffered
      byte[] response = new byte[MAXLENGTH];
      int len = -1;
      System.out.println("Start to fetch response");
      boolean getDate = cacher.state();
      while ((len = in.read(response)) != -1) {
        if (getDate) {
          String tmp = new String(response);
          // get first line and check state
          int firstEnter = tmp.indexOf("\r\n");
          String firstLine = tmp.substring(0, firstEnter);
          state = Integer.valueOf(firstLine.split(" ")[1]);
          int dateIndex = tmp.indexOf("Last-Modified");
          Pattern pattern;
          if(dateIndex == -1) {
            dateIndex = tmp.indexOf("Date");
            pattern = Pattern.compile("Date: (.*) GMT");
          }else {
            pattern = Pattern.compile("Last-Modified: (.*) GMT");
          }
          int dateEnd = tmp.substring(dateIndex).indexOf("\r\n") + dateIndex;
          // get the line with date
          String dateLine = tmp.substring(dateIndex, dateEnd);
          System.out.println(dateLine + "---------------------------------");
          Matcher matcher = pattern.matcher(dateLine);
          if (!matcher.matches()) {
            throw new IOException();
          }
          dateLine = matcher.group(1);
          date = dateFormat.parse(dateLine);
          getDate = false;
        }
        System.out.println("the http state for " + url + " is: " + state);
        // there is cache and not modified, read from cache
        if (cacher.state() && ((cacher.date(url) != null) && state == 304)) {
          System.out.println("*************try to get cache: " + url + "***********");
          File file = new File("cache/" + Md5.md5(url));
          FileInputStream input = new FileInputStream(file);
          byte[] fileTmp = new byte[MAXLENGTH];
          while (input.read(fileTmp) != -1) {
            responses.add(fileTmp);
            out_c.write(fileTmp);
            // change a new array address
            fileTmp = new byte[MAXLENGTH];
          }
          input.close();
          // if read from file, then just break the loop
          break;
        }
        // if the length is maximum
        if (len == MAXLENGTH) {
          responses.add(response);
          out_c.write(response);
        } else {
          // cut the empty part
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
    } catch (java.net.SocketTimeoutException e) {
      System.out.println("receive time running out");
    }
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

package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A request class, which contains the port and host of the destination, what's more, is contains a
 * whole http request body that can be forwarded.
 * 
 */
public class Request {
  private String url;
  private String host;
  private int port;
  private List<byte[]> requests = new ArrayList<>();
  private final Fish fish;
  private final Cacher cacher;
  private final int MAXLENGTH = 65536;
  private final DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HHHH:mm:ss ");
  /**
   * get host and port from the input stream, save the request.
   * 
   * @param in input stream from a client
   * @param fish lists that trying to do redirection
   * @param cacher manage the cache
   * @throws IOException
   * @throws InterruptedException
   */
  public Request(InputStream in, Fish fish, Cacher cacher) throws IOException, InterruptedException {
    this.fish = fish;
    this.cacher = cacher;
    // state = 0, means the url hasn't gotten yet
    int state = 0;
    // current input that did not being buffered
    byte[] request = new byte[MAXLENGTH];
    int len = -1;
    while ((len = in.read(request)) != -1) {
      if (state == 0) {
        String tmp = new String(request);
        int firstEnter = tmp.indexOf("\r\n");
        String firstLine = tmp.substring(0, firstEnter);
        int secondEnter = tmp.substring(firstEnter + 2).indexOf("\r\n") + firstEnter + 2;
        String secondLine = tmp.substring(firstEnter + 2, secondEnter);
        url = firstLine.split(" ")[1];
        String[] hosts = secondLine.split(" ")[1].split(":");
        //Pattern pattern = Pattern.compile("^http://(.*)(:[0-9]*)?(.*)?");
        host = hosts[0];
        // trying to redirect the host with port
        String host_modified = fish.Redirect(host);
        //Matcher matcher = pattern.matcher(host_modified);
        if (hosts.length == 1) {
          port = 80;
        } else {
          port = Integer.valueOf(hosts[1]);
        }
        if (!host_modified.equals(host)) {
        /*  if(!matcher.matches()) {
            throw new IOException();
          }*/
          System.out.println("redirected to: " + host_modified);
          // reconstruct HTTP head
          request = (firstLine.replace(host, host_modified) + "\r\nHost: "
              + host_modified + tmp.substring(secondEnter, len)).getBytes();
          len = request.length;
          //host = matcher.group(1).split("/")[0];
          host = host_modified;
          port = 80;
          url = url.replace(host, host_modified);
        }
        state = 1;
      }
      if (check(request, len)) {
        System.out.println("Read aok");
        byte[] tmp = new byte[len];
        System.arraycopy(request, 0, tmp, 0, len);
        requests.add(tmp);
        Date date;
        // there is cache, so add if-modified-since
        if((date = cacher.date(url)) != null) {
          byte[] request_tmp = new byte[len - 2];
          System.arraycopy(request, 0, request_tmp, 0, len - 2);
          String modified = new String(request_tmp) + "If-Modified-Since: " + dateFormat.format(date) + " GMT\r\n\r\n";
          tmp = (modified).getBytes();
        }
        break;
      }
      requests.add(request);
      request = new byte[MAXLENGTH];
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
   * return the host of the destination.
   * 
   * @return host of the destination
   */
  public String getHost() {
    return host;
  }

  /**
   * return the port number.
   * 
   * @return port number of the destination, usually be 80, if it is http protocol
   */
  public int getPort() {
    return port;
  }

  /**
   * Return the whole requests.
   * 
   * @return the whole requests.
   */
  public List<byte[]> getRequest() {
    System.out.println("Return a request");
    return new ArrayList<byte[]>(requests);
  }
  
  /**
   * return the url of current request
   * @return the url of current request
   */
  public String getUrl() {
    return url;
  }
}

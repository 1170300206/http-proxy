package proxy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import proxy.Request;
import proxy.Response;

/**
 * connect to one client, process this client's request
 *
 */
public class ClientSocket implements Runnable {
  /**
   * TIMEOUT_C: client socket's timeout TIMEOUT_S: server socket's timeout userFilter: for filter
   * the users
   */
  private final Socket cSocket;
  private final int TIMEOUT_C = 10000;
  private final int TIMEOUT_S = 10000;
  private final FireWall fireWall;
  private final Fish fish;
  private final Cacher cacher;
  private final int MAXLENGTH = 65535;
  private Response response;
  private Request request;

  /**
   * 
   * @param cSocket client socket
   * @param fireWall fireWall that check if the destination host is blocked
   * @param fish lists that trying to do redirection
   * @param cacher manage the cache
   */
  public ClientSocket(Socket cSocket, FireWall fireWall, Fish fish, Cacher cacher) {
    this.cSocket = cSocket;
    this.fireWall = fireWall;
    this.fish = fish;
    this.cacher = cacher;
    try {
      this.cSocket.setSoTimeout(TIMEOUT_C);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  private List<byte[]> pipe(String host, int port, List<byte[]> requests, OutputStream out_c) {
    List<byte[]> responses = new ArrayList<>();
    Socket forwardSocket;
    try {
      forwardSocket = new Socket(host, port);
      forwardSocket.setSoTimeout(TIMEOUT_S);
      System.out.println("connect to server, the host is: " + host + ", the port is: " + port);
      OutputStream out = forwardSocket.getOutputStream();
      for (byte[] request : requests) {
        out.write(request);
      }
      InputStream in = forwardSocket.getInputStream();
      response = new Response(in, out_c, cacher.state());
      // not modified, read from cache
      if (response.state() == 304 && cacher.state()) {
        File file = new File("cache/" + URLEncoder.encode(request.getUrl()));
        FileInputStream input = new FileInputStream(file);
        byte[] tmp = new byte[MAXLENGTH];
        while (input.read(tmp) != -1) {
          responses.add(tmp);
        }
        input.close();
      } else {
        responses = response.getResponse();
      }
      forwardSocket.close();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (java.text.ParseException e) {
      e.printStackTrace();
    }
    return responses;
  }

  @Override
  public void run() {
    System.out.println("Start a new thread");
    try {
      InputStream in = cSocket.getInputStream();
      // get port and url
      request = new Request(in, fish, cacher);
      // if the host is a forbidden host
      if (fireWall.isFiltered(request.getHost())) {
        System.out.println("host: " + request.getHost() + " is blocked");
        cSocket.close();
      }
      OutputStream out = cSocket.getOutputStream();
      // get responses
      List<byte[]> responses =
          pipe(request.getHost(), request.getPort(), request.getRequest(), out);
      // write responses regardless of the cacher
      for (byte[] response : responses) {
        out.write(response);
      }
      if (response.state() != 304 && cacher.state()) {
        // save cache
        File file = new File("cache/" + URLEncoder.encode(request.getUrl()));
        FileOutputStream fo = new FileOutputStream(file, true);
        BufferedOutputStream bf = new BufferedOutputStream(fo);
        for (byte[] bytes : responses) {
          bf.write(bytes);
        }
        bf.close();
        fo.close();
      }
      cacher.set(request.getUrl(), response.date());
      cacher.save();
      cSocket.close();
      System.out.println("Thread closed");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}

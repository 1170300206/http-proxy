package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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

  /**
   * 
   * @param cSocket client socket
   * @param fireWall fireWall that check if the destination host is blocked
   * @param fish lists that trying to do redirection
   */
  public ClientSocket(Socket cSocket, FireWall fireWall, Fish fish) {
    this.cSocket = cSocket;
    this.fireWall = fireWall;
    this.fish = fish;
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
      Response response = new Response(in, out_c);
      responses = response.getResponse();
      forwardSocket.close();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
      Request request = new Request(in, fish);
      // if the host is a forbidden host
      if(fireWall.isFiltered(request.getHost())) {
        System.out.println("host: "+ request.getHost() + " is blocked");
        cSocket.close();
      }
      OutputStream out = cSocket.getOutputStream();
      // get responses
      List<byte[]> responses =
          pipe(request.getHost(), request.getPort(), request.getRequest(), out);
      /*
       * for(byte[] response: responses) { out.write(response); }
       */
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

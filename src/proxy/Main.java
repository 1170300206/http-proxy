package proxy;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import proxy.ListenSocket;

public class Main{
  private static final boolean USER_FILTER = false;
  private static final boolean FIRE_WALL = false;
  private static final boolean FISH = false;
  private static final boolean CACHE = false;
  public static void main(String[] arg) {
    try {
      ListenSocket serverSocket = new ListenSocket(8388, USER_FILTER, FIRE_WALL, FISH, CACHE);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
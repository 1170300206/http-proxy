package proxy;
import java.io.IOException;
import java.text.ParseException;
import proxy.ListenSocket;

public class Main{
  private static final boolean USER_FILTER = false;
  private static final boolean FIRE_WALL = false;
  private static final boolean FISH = true;
  private static final boolean CACHE = true;
  public static void main(String[] arg) throws ParseException {
    try {
      ListenSocket serverSocket = new ListenSocket(8388, USER_FILTER, FIRE_WALL, FISH, CACHE);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
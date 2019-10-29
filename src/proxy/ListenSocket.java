package proxy;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * To initiate a server socket and listen the client's requests
 *
 */
public class ListenSocket{
  private ServerSocket serverSocket;
  private final UserFilter userFilter;
  private final FireWall fireWall;
  private final Fish fish;
  private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
  /**
   * Initiate the server listener 
   * @param portNum port number, 0 < portNum <= 65535
   * @param FILTER_USER for filter the users, set to true to open filter
   * @param FIRE_WALL for filter the web sites, set to true to open filter
   * @param FISH for redirect some of the web sites
   * @throws IOException when port number is illegal
   */
  public ListenSocket(int portNum, boolean FILTER_USER, boolean FIRE_WALL, boolean FISH) throws IOException{
    userFilter = new UserFilter(FILTER_USER);
    fireWall = new FireWall(FIRE_WALL);
    fish = new Fish(FISH);
    if(portNum < 0 || portNum > 65535) {
      throw new IOException("wrong port number!");
    }
    serverSocket = new ServerSocket(portNum);
    Socket socket = null;
    // listen the client's requests
    while(true) {
      try {
        socket = serverSocket.accept();
        // if the user is blocked
        if(userFilter.isFiltered(socket.getInetAddress())) {
          System.out.println("User: "+ socket.getInetAddress() + " is blocked");
          return;
        }
        System.out.println("get one socket: "+ socket.getInetAddress());
        ClientSocket clientSocket = new ClientSocket(socket, fireWall, fish);
        cachedThreadPool.execute(clientSocket);
      } catch (IOException e) {
        throw e;
      }
    }
  }
}
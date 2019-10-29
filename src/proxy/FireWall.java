package proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * Use to block some of the web site.
 */
public class FireWall{
private Set<String> FireWallList;
  
  /**
   * initiate the fire wall list
   * @param open when it's true, open the option to filter the web site
   */
  public FireWall(boolean open) {
    FireWallList = new HashSet<>();
    if(open) {
      File fireWall = new File("rules/fire_wall.txt");
      InputStrategy input;
      try {
        input = InputStrategy.input(fireWall);
        String tmp;
        while((tmp = input.nextLine()) != null) {
          FireWallList.add(tmp);
        }
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Check if the web-site's host is in the list
   * @param host the host of the web site
   * @return true if the host is on the list, false if not
   */
  public boolean isFiltered(String host){
    System.out.println("checked host: " + host);
    if(FireWallList.contains(host)) {
      return true;
    }
    return false;
  }
}
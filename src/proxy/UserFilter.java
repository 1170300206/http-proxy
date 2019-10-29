package proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter those who are forbidden to access to the Internet.
 *
 */
public class UserFilter{
  private Set<String> userList;
  
  /**
   * initiate the user-filter list
   * @param open when it's true, open the option to filter the user
   */
  public UserFilter(boolean open) {
    userList = new HashSet<>();
    if(open) {
      File user = new File("rules/user_rules.txt");
      InputStrategy input;
      try {
        input = InputStrategy.input(user);
        String tmp;
        while((tmp = input.nextLine()) != null) {
          userList.add(tmp);
        }
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Check if the user is in the list
   * @param user the user's host
   * @return true if the user is in the list, false if not
   */
  public boolean isFiltered(InetAddress user){
    if(userList.contains(user.toString().replace("/", ""))) {
      return true;
    }
    return false;
  }
}
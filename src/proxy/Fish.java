package proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Fish{
  private final Map<String, String> fish;
  
  /**
   * initiate the redirect pairs.
   * @param open the option to open the redirection
   */
  public Fish(boolean open) {
    fish = new HashMap<>();
    if(open) {
      File fishes = new File("rules/fish.txt");
      InputStrategy input;
      try {
        input = InputStrategy.input(fishes);
        String tmp, pairs[];
        while((tmp = input.nextLine()) != null) {
          pairs = tmp.split(" ");
          fish.put(pairs[0], pairs[1]);
        }
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Check if the web-site's host is in the list and can be redirect.
   * @param host the host of the web site
   * @return the redirect host if there exists, original host if there is not.
   */
  public String Redirect(String host){
    System.out.println("checked host: " + host);
    if(fish.containsKey(host)) {
      return fish.get(host);
    }
    return host;
  }
}
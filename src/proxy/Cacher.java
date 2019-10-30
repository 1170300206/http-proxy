package proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Cacher {
  private final Map<String, Date> cachedates;
  private final boolean open;
  private final DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HHHH:mm:ss ");
  private final File cacheLists = new File("cache/cache.txt");

  /**
   * initiate the cache option
   * 
   * @param open true to open the cache
   */
  public Cacher(boolean open) {
    this.open = open;
    cachedates = new HashMap<>();
    // read all the caches
    if (open) {
      InputStrategy input;
      try {
        input = InputStrategy.input(cacheLists);
        String tmp;
        while ((tmp = input.nextLine()) != null) {
          String[] pair = tmp.split("_");
          cachedates.put(pair[0], formatter.parse(pair[1]));
        }
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * Return the date version of the url that saved.
   * 
   * @param url the target url
   * @return Date of the url's version, null if there is no such cache
   */
  public Date date(String url) {
    if (open) {
      return cachedates.getOrDefault(url, null);
    }
    return null;
  }

  /**
   * Save or update the url-date pair
   * 
   * @param url the page that need to be saved
   * @param date the time of the page that lastly being modified.
   * @return true if successfully modified, false if not
   */
  public boolean set(String url, Date date) {
    if (open) {
      Date date1 = cachedates.put(url, date);
      if (date1.equals(date)) {
        return false;
      }else {
        return true;
      }
    }
    return false;
  }

  /**
   * save the current relationship.
   */
  public void save() {
    if (open) {
      try {
        OutputStrategy out = OutputStrategy.emptyInstance(cacheLists, false);
        for (Entry<String, Date> entry : cachedates.entrySet()) {
          out.outPut(entry.getKey() + "_" + formatter.format(entry.getValue()));
        }
        out.close();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  /**
   * return the state of the cacher
   * @return true if the cacher is on, false if not
   */
  public boolean state() {
    return open;
  }
}

package proxy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Cacher {
  private final Map<String, Date> cachedates;
  private final boolean open;
  private final DateFormat formatter =
      new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
  private final File cacheLists = new File("cache/cache.txt");

  /**
   * initiate the cache option
   * 
   * @param open true to open the cache
   */
  public Cacher(boolean open) {
    this.open = open;
    cachedates = new ConcurrentHashMap<>();
    // read all the caches
    if (open) {
      InputStrategy input;
      try {
        input = InputStrategy.input(cacheLists);
        String tmp;
        while ((tmp = input.nextLine()) != null) {
          String[] pair = tmp.split("     ");
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
  public boolean set(String url, Date date, List<byte[]> responses, int state) {
    if (open) {
      if (url.length() == 0) {
        System.out.println("wrong url!!!!");
      }
      Date date1 = cachedates.put(url, date);
      // save cache
      System.out.println("save cache: " + url);
      File file = new File("cache/" + Md5.md5(url));
      FileOutputStream fo;
      try {
        fo = new FileOutputStream(file, false );
        BufferedOutputStream bf = new BufferedOutputStream(fo);
        for (byte[] bytes : responses) {
          bf.write(bytes);
        }
        bf.close();
        fo.close();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (date.equals(date1)) {
        return false;
      } else {
        if (state == 200) {
        }
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
          out.outPut(entry.getKey() + "     " + formatter.format(entry.getValue()) + '\n');
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
   * 
   * @return true if the cacher is on, false if not
   */
  public boolean state() {
    return open;
  }
}

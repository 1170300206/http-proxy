package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Updater implements Runnable {
  private InputStream in;
  private OutputStream out;
  private boolean ifCheck;

  public Updater(InputStream in, OutputStream out, boolean ifCheck) {
    this.in = in;
    this.out = out;
    this.ifCheck = ifCheck;
  }

  @Override
  public void run() {
    int c1 = -1;
    int c2 = -1;
    int c3 = -1;
    int c4 = -1;
    int c;
    try {
      //int i = 0;
      while ((c = in.read()) != -1) {
        //System.out.println(i++);
        out.write(c);
        c1 = c2;
         c2 = c3;
        c3 = c4;
        c4 = c;
        if (ifCheck) {
          System.out.println(c1 + "-" + c2 + "-" + c3 + "-" + c4 + "-");
          if((c1 == 13 && c2 == 10) && (c3 == 13 && c2 == 10)) {
            break;
          }
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}

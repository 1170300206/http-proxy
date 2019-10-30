package proxy;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

  /**
   * Return a md5 string
   * @param plainText the text that needed to be encoded
   * @return md5 string
   */
  public static String md5(String plainText) {
      byte[] secretBytes = null;
      try { 
          MessageDigest md = MessageDigest.getInstance("MD5");
          //encode the plainText
          md.update(plainText.getBytes());
          //get the Bytes encoded
          secretBytes = md.digest();
      } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException();
      }
      //change to HEX
      String md5code = new BigInteger(1, secretBytes).toString(16);
      // fill to lenngth 32
      for (int i = 0; i < 32 - md5code.length(); i++) {
          md5code = "0" + md5code;
      }
      return md5code;
  }

}
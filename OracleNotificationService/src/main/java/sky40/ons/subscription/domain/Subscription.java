package sky40.ons.subscription.domain;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Defines a subscription. Data class. Gives full information about the
 * subscription, including private data of the subscriber.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Getter
@Setter
@Slf4j
public class Subscription {

  private Long id;
  private String name;
  private String url;
  private int timeout;
  private String sessionToken;

  protected Subscription() {
  }

  public Subscription(Long id, String name, String url, int timeout, String sessionToken) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.timeout = timeout;
    this.sessionToken = sessionToken;
  }

  private static long lastId = 0;

  /**
   *
   * @return next id for a subscription
   */
  public static Long nextId() {

    long id = new Date().getTime();
    while (id <= lastId) {
      id++;
    }
    lastId = id;
    return id;
  }

  public static String nextToken() {
    try {
      long time = new Date().getTime();
      String plaintext = "geheim" + time;
      MessageDigest m = MessageDigest.getInstance("MD5");
      m.reset();
      m.update(plaintext.getBytes());
      byte[] digest = m.digest();
      BigInteger bigInt = new BigInteger(1, digest);
      String hashtext = bigInt.toString(16);
      // Now we need to zero pad it if you actually want the full 32 chars.
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }

      return hashtext;
    } catch (NoSuchAlgorithmException ex) {
      log.error("Internal error. Token creation failed.");
    }
    return "noToken";
  }

}

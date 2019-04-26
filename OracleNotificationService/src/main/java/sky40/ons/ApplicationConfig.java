package sky40.ons;

import java.util.ArrayList;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Configuration class for the application. Takes values out of the environment
 * (mainly application.properties file in main/resources package). Spring will
 * fill in the values at runtime if configured right.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "ons")
@Component
public class ApplicationConfig {

  public static class Oracle {

    String hostname;
    int port;
    String user;
    String pass;
    String database;

    public String getHostname() {
      return hostname;
    }

    public void setHostname(String hostname) {
      this.hostname = hostname;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getUser() {
      return user;
    }

    public void setUser(String user) {
      this.user = user;
    }

    public String getPass() {
      return pass;
    }

    public void setPass(String pass) {
      this.pass = pass;
    }

    public String getDatabase() {
      return database;
    }

    public void setDatabase(String database) {
      this.database = database;
    }

  }

  private Oracle oracle;

  public Oracle getOracle() {
    return oracle;
  }

  public void setOracle(Oracle oracle) {
    this.oracle = oracle;
  }

  private Notification notification;

  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  public Notification getNotification() {
    return notification;
  }

  public static class Notification {

    String clientHostname;
    int clientPort;
    ArrayList<String> tableName;

    public String getClientHostname() {
      return clientHostname;
    }

    public int getClientPort() {
      return clientPort;
    }

    public ArrayList<String> getTableName() {
      return tableName;
    }

    public void setClientHostname(String clientHostname) {
      this.clientHostname = clientHostname;
    }

    public void setClientPort(int clientPort) {
      this.clientPort = clientPort;
    }

    public void setTableName(ArrayList<String> tableName) {
      this.tableName = tableName;
    }
  }

  private Push push;

  public Push getPush() {
    return push;
  }

  public void setPush(Push push) {
    this.push = push;
  }

  /**
   * Configures the list of push targets.
   */
  public static class Push {

    private boolean enabled;
    private ArrayList<String> endpoint;

    public ArrayList<String> getEndpoint() {
      return endpoint;
    }

    public void setEndpoint(ArrayList<String> endpoint) {
      this.endpoint = endpoint;
    }

    /**
     * Indicates if pushing to predefined targets is enabled.
     *
     * @return if is enabled
     */
    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

  }

}

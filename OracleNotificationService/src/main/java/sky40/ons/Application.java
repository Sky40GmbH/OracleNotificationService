package sky40.ons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import sky40.ons.listener.DatabaseManager;
import sky40.ons.listener.ONSchangeListener;

/**
 * Main class and entry point into the application, if not started in a hosted
 * environment (running outside an application server).
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@SpringBootApplication
@Slf4j
public class Application {

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
    ONSchangeListener listener = ctx.getBean(ONSchangeListener.class);
    DatabaseManager dbm = ctx.getBean(DatabaseManager.class);
    dbm.startup(listener);
  }

}

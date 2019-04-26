package sky40.ons.subscription.web.controller;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sky40.ons.subscription.domain.Result;
import sky40.ons.subscription.svc.ConsumerService;

/**
 * Controller class for teh REST based web interface. Delegates to the internal
 * service.
 *
 * @author Hendrik Stilke  {@literal (Hendrik.Stilke@sky40.de)}
 */
@RestController
@RequestMapping("/consumer")
@Slf4j
public class ConsumerController {

  @Autowired
  ConsumerService svc;

  @Autowired
  Environment environment;

  private String token;

  @GetMapping("/start")
  public Result<String> start() {

    String port = environment.getProperty("consumer.port");
    String ip = environment.getProperty("consumer.endpoint");

    String endpoint = ip + ":" + port + "/consumer/messagereceiver";
    log.info("Subscribing for a consumer on endpoint: " + endpoint);

    ConsumerService.CreateSubscriptionParams subscriptionRequestParams = new ConsumerService.CreateSubscriptionParams("ConsumerClient" + new Date().getTime(), endpoint, 900);

    ConsumerService.SubscriptionResult result = svc.subscribe(subscriptionRequestParams);
    this.token = result.getResponse().getSessionToken();
    // subscribe this client

    log.info("Subscription sent. Waiting for messages ...");

    String ret = "Subscription ok.";
    return new Result<>(ret);
  }

  @PostMapping("/messagereceiver")
  public Result<String> receiveMessage(@RequestParam("message") String message) {
    log.info("Consumer received a message: " + message);

    String ret = "message received at " + new Date().toGMTString();
    return new Result<>(ret);
  }

}

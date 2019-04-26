package sky40.ons.subscription.web.controller;

import sky40.ons.subscription.domain.Result;
import sky40.ons.subscription.domain.Subscription;
import sky40.ons.subscription.domain.SubscriptionInfo;
import sky40.ons.subscription.svc.SubscriptionService;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST based controller to manage subscriptions via the REST endpoint
 * interface. Uses the internal service to delegate requests.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class SubscriptionController {

  private final SubscriptionService svc;

  @Autowired
  public SubscriptionController(SubscriptionService svc) {
    this.svc = svc;
  }

  @GetMapping("/subscription/{id}")
  public Result<SubscriptionInfo> getSubscriberById(@PathVariable Long id) {
    SubscriptionInfo ret = new SubscriptionInfo(svc.findSubscriptionById(id));
    return new Result<>(ret);
  }

  @GetMapping("/subscription")
  public Result<Collection<SubscriptionInfo>> getSubscriberAll() {
    Collection<SubscriptionInfo> ret = svc.findSubscriptionInfoAll();
    return new Result<>(ret);
  }

  @DeleteMapping("/subscription/{token}")
  public Result<SubscriptionInfo> deleteSubscriber(@PathVariable String token) {
    SubscriptionInfo ret = svc.deleteSubscription(token);
    return new Result<>(ret);
  }

  @PostMapping("/subscription")
  public Result<Subscription> createSubscriber(
          @RequestParam("name") String name,
          @RequestParam("url") String url,
          @RequestParam("timeout") int timeout
  ) {
    try {
      Subscription ret = svc.createSubscription(name, url, timeout);
      log.info("Subscription created for name " + name);
      return new Result<>(ret);
    } catch (Exception ex) {
      return new Result<>(ex);
    }
  }
}

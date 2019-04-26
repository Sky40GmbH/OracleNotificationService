package sky40.ons.listener;

import sky40.ons.ApplicationConfig;
import sky40.ons.subscription.domain.Subscription;
import sky40.ons.subscription.svc.SubscriptionService;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Notifies the subscribers and push targets about changes. Uses asynchronous
 * notification and thread pooling via reactive streams to decouple the
 * notifications of each client.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Service
@Slf4j
public class ONSchangeNotifier {

  @Autowired
  SubscriptionService subSvc;

  @Autowired
  ApplicationConfig config;

  public void notify(String message) {
    notifySubscribers(message);
    notifyPush(message);
  }

  private void notifyPush(String message) {
    log.info("Notify via PUSH-service.");
    if (config.getPush().isEnabled()) {
      for (String url : config.getPush().getEndpoint()) {
        sendMessage(url, message);
      }
    } else {
      log.info("Not pushing to targets. Feature is disabled.");
    }
  }

  private void notifySubscribers(String message) {
    log.info("Notify via Subscriber-service.");
    Collection<Subscription> subscriptions = subSvc.findSubscriptionAll();
    for (Subscription sub : subscriptions) {
      String url = sub.getUrl();
      sendMessage(url, message);
    }
  }

  private void sendMessage(String url, String message) {
    log.info("Sending message to endpoint " + url + " ...");
    LinkedMultiValueMap map = new LinkedMultiValueMap();
    map.add("message", message);
    WebClient webClient = getWebClient(url);
    Mono<String> mono = webClient.post()
            .body(BodyInserters.fromMultipartData(map))
            .retrieve()
            .bodyToMono(String.class);
    mono.subscribe();
    log.info("... message sent.");
  }

  private WebClient getWebClient(String endpoint) {
    WebClient ret;
    ret = WebClient.builder().baseUrl(endpoint).build();
    return ret;
  }

}

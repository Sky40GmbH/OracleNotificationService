package sky40.ons.subscription.svc;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sky40.ons.subscription.domain.Result;
import sky40.ons.subscription.domain.Subscription;

/**
 * A service for the actions of the {@link ConsumerController}. Internal service
 * class.
 *
 * @author Hendrik Stilke  {@literal (Hendrik.Stilke@sky40.de)}
 */
@Service
@Slf4j
public class ConsumerService {

  @Autowired
  Environment environment;

  /**
   * The method suffix to call for subscription.
   */
  private static final String ENDPOINT_SUFFIX = "/api";

  private String getServiceEndpoint() {
    String port = environment.getProperty("service.port");
    String ip = environment.getProperty("service.endpoint");

    String endpoint = ip + ":" + port + ENDPOINT_SUFFIX;
    return endpoint;
  }

  /**
   * Do not use this! Use the getter for lazy loading instead!
   */
  private WebClient webClient;

  /**
   * Get webclient. Lazy loading.
   *
   * @return a webclient instance
   */
  public WebClient getWebClient() {
    if (this.webClient == null) {
      this.webClient = WebClient.builder().baseUrl(getServiceEndpoint()).build();
    }

    return this.webClient;
  }

  public SubscriptionResult subscribe(CreateSubscriptionParams subscriptionRequestParams) {

    getWebClient();

    LinkedMultiValueMap map = new LinkedMultiValueMap();
    map.add("name", subscriptionRequestParams.getName());
    map.add("url", subscriptionRequestParams.getUrl());
    map.add("timeout", subscriptionRequestParams.getTimeout());

    Mono<SubscriptionResult> mono = this.webClient.post()
            .uri("/subscription")
            .body(BodyInserters.fromMultipartData(map))
            .retrieve()
            .bodyToMono(SubscriptionResult.class);

    SubscriptionResult response = mono.block();
    return response;
  }

  public static class SubscriptionResult extends Result<Subscription> {

    public SubscriptionResult() {
      super();
    }

    public SubscriptionResult(Subscription sub) {
      super(sub);
    }

    public SubscriptionResult(String error) {
      super(new RuntimeException(error));
    }

    public void setCreated(Date created) {
      this.created = created;
    }

    public void setError(String error) {
      this.error = error;
    }

    public void setResponse(Subscription response) {
      this.response = response;
    }

  }

  public static class CreateSubscriptionParams {

    private final String name;
    private final String url;
    private final int timeout;

    public CreateSubscriptionParams(String name, String url, int timeout) {
      this.name = name;
      this.url = url;
      this.timeout = timeout;
    }

    public String getName() {
      return name;
    }

    public String getUrl() {
      return url;
    }

    public int getTimeout() {
      return timeout;
    }

  }

}

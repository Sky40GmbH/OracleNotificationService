package sky40.ons.subscription.svc;

import sky40.ons.subscription.domain.Subscription;
import sky40.ons.subscription.domain.SubscriptionInfo;
import sky40.ons.subscription.repo.SubscriptionRepository;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Internal service to handle operations on subscriptions. Keeps the business
 * logic when working with subsriptions.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Service
public class SubscriptionService {

  private final SubscriptionRepository repository;

  @Autowired
  public SubscriptionService(SubscriptionRepository userRepository) {
    this.repository = userRepository;
  }

  public Subscription findSubscriptionById(Long id) {
    return repository.findById(id);
  }

  public Collection<SubscriptionInfo> findSubscriptionInfoAll() {
    ArrayList<SubscriptionInfo> ret = new ArrayList<>();
    for (Subscription sub : repository.getAll()) {
      ret.add(new SubscriptionInfo(sub));
    }
    return ret;
  }

  public Collection<Subscription> findSubscriptionAll() {
    return new ArrayList<>(repository.getAll());
  }

  /**
   * Delete the subscription with the token.
   * @param token the token (/handle)
   * @return informtion about the deleted subscription.
   */
  public SubscriptionInfo deleteSubscription(String token) {
    Subscription sub = this.repository.findByToken(token);
    if (sub != null) {
      this.repository.delete(sub.getId());
      return new SubscriptionInfo(sub);
    }
    throw new RuntimeException("Subscription not found.");
  }

  /**
   * Create new subscription.
   *
   * @param name
   * @param url
   * @param timeout
   * @return
   */
  public Subscription createSubscription(String name, String url, int timeout) {
    Subscription sub = this.repository.findByName(name);
    if (sub != null) {
      throw new RuntimeException("Subscription with name " + name + " already exists.");
    }

    sub = new Subscription(Subscription.nextId(), name, url, timeout, Subscription.nextToken());
    return this.repository.add(sub);
  }
}

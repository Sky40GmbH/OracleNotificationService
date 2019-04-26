package sky40.ons.subscription.repo;

import sky40.ons.subscription.domain.Subscription;
import java.util.Collection;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * Repository (DAO pattern) for the subscription class.
 * 
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Repository
@Slf4j
public class SubscriptionRepository {

  private final HashMap<Long, Subscription> subscribers = new HashMap<>();

  /**
   *
   * @return list of all subscribers
   */
  public Collection<Subscription> getAll() {
    return subscribers.values();
  }

  /**
   * Adds a new subscription.
   *
   * @param sub
   * @return The subscription or NULL if it could not be added.
   */
  public Subscription add(Subscription sub) {
    if (!subscribers.containsKey(sub.getId())) {
      subscribers.put(sub.getId(), sub);
      return sub;
    }
    return null;
  }

  /**
   * Delete the subscription with id.
   *
   * @param id
   * @return
   */
  public Subscription delete(long id) {
    if (subscribers.containsKey(id)) {
      return subscribers.remove(id);
    }
    return null;
  }

  /**
   * Get subscription by the token.
   *
   * @param token
   * @return
   */
  public Subscription findByToken(String token) {
    for (Subscription sub : subscribers.values()) {
      if (token.equalsIgnoreCase(sub.getSessionToken())) {
        return sub;
      }
    }
    return null;
  }

  /**
   * Get subscription by name.
   * 
   * @param name
   * @return 
   */
  public Subscription findByName(String name) {
    for (Subscription sub : subscribers.values()) {
      if (name.equalsIgnoreCase(sub.getName())) {
        return sub;
      }
    }
    return null;
  }

  /**
   * Get subscription by id.
   * @param id
   * @return 
   */
  public Subscription findById(Long id) {
    return subscribers.get(id);
  }

}

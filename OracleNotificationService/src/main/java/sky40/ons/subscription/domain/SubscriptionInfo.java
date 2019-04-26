package sky40.ons.subscription.domain;

/**
 * Defines an information about a subscription. Reveals only public information
 * for listing purposes.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
public class SubscriptionInfo {

  private final Subscription sub;

  public SubscriptionInfo(Subscription sub) {
    this.sub = sub;
    if (sub == null) {
      throw new RuntimeException("Subscription is null.");
    }
  }

  public long getId() {
    return sub.getId();
  }

  public String getName() {
    return sub.getName();
  }

}

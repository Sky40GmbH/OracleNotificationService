package sky40.ons.subscription.domain;

import java.util.Date;

/**
 * Generic return-Type for web requests. Encapsulating a result and/or error.
 *
 * @author Hendrik Stilke  {@literal (Hendrik.Stilke@sky40.de)}
 * @param <T> The type of result to wrap.
 */
public class Result<T> {

  protected Date created;
  protected String error;
  protected T response;

  public Date getCreated() {
    return created;
  }

  public String getError() {
    return error;
  }

  public T getResponse() {
    return response;
  }

  protected Result() {
    this.created = new Date();
  }

  public Result(Exception error) {
    this();
    this.error = error.getMessage();
  }

  public Result(T response) {
    this();
    this.response = response;
  }

}

package sky40.ons.domain;

import sky40.ons.util.JsonBuilder;

/**
 * Base class for domain objects, that allow serialization to JSON.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
public abstract class BaseObject {

  /**
   * The json-builder.
   */
  public static JsonBuilder jsonBuilder = JsonBuilder.singleton();

  public static final String EMPTY = "";

  /**
   * Convert domain object into json representation. Enclosed in object
   * brackets.
   *
   * @return json representation of domain object.
   */
  public String toJson() {
    return toJson(true);
  }

  /**
   * Convert domain object into json representation.
   *
   * @param isEnclosed , true = enclosed in object brackets, false = chained
   * key/value-pairs to be used by inherited type.
   * @return json representation of domain object
   */
  abstract protected String toJson(boolean isEnclosed);

  @Override
  public String toString() {
    return toJson();
  }

}

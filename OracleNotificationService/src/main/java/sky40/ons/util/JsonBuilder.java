package sky40.ons.util;

import sky40.ons.domain.BaseObject;
import java.util.Collection;

/**
 * Convenience class to build json-formatted object representations without the
 * need of costly reflection. Uses the {@link BaseObject} to differentiate if an
 * object itself is serializable into json format.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
public class JsonBuilder {

  public static JsonBuilder singleton() {
    return theInstance;
  }

  private static final String OBJECT_END = "}";

  private static final String OBJECT_START = "{";

  private static final String ARRAY_END = "]";

  private static final String ARRAY_START = "[";

  /**
   * Constant for a comma string: ",".
   */
  public static final String COMMA = ",";

  /**
   * Constant for a colon string: ":".
   */
  public static final String COLON = ":";

  /**
   * Constant for a quote string: "\"".
   */
  public static final String QUOTE = "\"";

  private final static JsonBuilder theInstance = new JsonBuilder();

  /**
   * Build a json-array from the list.
   *
   * @param isQuoted indicates if the elements of the list shall be extra
   * quoted.
   * @param list elements to be in the array
   * @return json-array representation
   */
  public String array(boolean isQuoted, Collection<?> list) {
    return ARRAY_START + chain(isQuoted, list) + ARRAY_END;
  }

  /**
   * Build a json-array from the list.
   *
   * @param list elements to put in the array
   * @return json-array representation
   */
  public String array(Collection<?> list) {
    if (list == null) {
      return ARRAY_START + ARRAY_END;
    }
    return ARRAY_START + chain(list) + ARRAY_END;
  }

  /**
   * Build a json-array from the list.
   *
   * @param base (base)elements to put in the array
   * @return json-array representation
   */
  public String array(BaseObject... base) {
    return ARRAY_START + chain(base) + ARRAY_END;
  }

  /**
   * Build a json-array from the list.
   *
   * @param str strings to put in the array
   * @return json-array representation
   */
  public String array(String... str) {
    return ARRAY_START + chain(str) + ARRAY_END;
  }

  /**
   * Build a json-array from the list.
   *
   * @param str strings to put in the array
   * @param isQuoted indicates if the elements of the list shall be extra
   * quoted.
   *
   * @return json-array representation
   */
  public String array(boolean isQuoted, String... str) {
    return ARRAY_START + chain(isQuoted, str) + ARRAY_END;
  }

  /**
   * Chains the (domain) objects.
   *
   * @param base collection of base objects to chain
   * @return chained objects
   */
  public String chain(Collection<?> base) {
    return chain(false, base);
  }

  /**
   * Chains the (domain) objects.
   *
   * @param isPutInQuotes set true if objects shall be set into quotes (e.g.
   * serialization of enum values)
   * @param base collection of (base) objects
   * @return chained objects
   */
  public String chain(boolean isPutInQuotes, Collection<?> base) {
    StringBuilder ret = new StringBuilder();

    int i = 0;
    for (Object b : base) {
      if (b != null) {
        String add;
        if (b instanceof BaseObject) {
          add = ((BaseObject) b).toJson();
        } else {
          add = String.valueOf(b);
        }
        if (add.length() > 0) {
          if (i++ > 0) {
            ret.append(COMMA);
          }
          if (isPutInQuotes) {
            ret.append(QUOTE).append(add).append(QUOTE);
          } else {
            ret.append(add);
          }
        }
      }
    }

    return ret.toString();
  }

  /**
   * Chains the domain objects.
   *
   * @param base (base) objects to chain
   * @return chained objects
   */
  public String chain(BaseObject... base) {
    StringBuilder ret = new StringBuilder();

    int i = 0;
    for (BaseObject b : base) {
      if (b != null) {
        String add = b.toJson();
        if (add.length() > 0) {
          if (i++ > 0) {
            ret.append(COMMA);
          }
          ret.append(add);
        }
      }
    }

    return ret.toString();
  }

  /**
   * Chains the key/value-pairs.
   *
   * @param pair pairs to chain. pairs should be a valid json pair in the form
   * key:"value" or key:{... json object ...}
   * @return chained strings
   */
  public String chain(String... pair) {
    return chain(false, pair);
  }

  /**
   * Chains the key/value-pairs.
   *
   * @param isQuoted indicates if the Strings to chain shall be in quotes.
   * @param pair pairs to chain. pairs should be a valid json pair in the form
   * key:"value" or key:{... json object ...}
   * @return chained strings
   */
  public String chain(boolean isQuoted, String... pair) {
    StringBuilder ret = new StringBuilder();

    int i = 0;
    if (pair != null) {
      for (String s : pair) {
        if (s != null) {
          if (s.length() > 0) {
            if (i++ > 0) {
              ret.append(COMMA);
            }
            if (isQuoted) {
              ret.append(QUOTE).append(s).append(QUOTE);
            } else {
              ret.append(s);
            }
          }
        }
      }
    }

    return ret.toString();
  }

  /**
   * Make a json object from the collection of attributes/objects.
   *
   * @param base collection of elements to make an object from
   * @return json object representation
   */
  public String object(Collection<?> base) {
    return OBJECT_START + chain(base) + OBJECT_END;
  }

  /**
   * Make a json object from the collection of attributes/objects.
   *
   * @param base collection of elements to make an object from
   * @return json object representation
   */
  public String object(BaseObject... base) {
    return OBJECT_START + chain(base) + OBJECT_END;
  }

  /**
   * Make a json object from the collection of attributes/objects.
   *
   * @param str collection of strings to make an object from
   * @return json object representation
   */
  public String object(String... str) {
    return OBJECT_START + chain(str) + OBJECT_END;
  }

  /**
   * Build json representation of a pair.
   *
   * @param key key value of json pair
   * @param value value of the json pair
   * @return json string representation of pair like key:"value"
   */
  public String pair(String key, Object value) {
    StringBuilder ret = new StringBuilder();

    if (value != null) {
      if (value instanceof String) {
        String str = (String) value;
        str = str.replace("\"", "''"); // remove ", because json parser will fail here!
        str = str.replace("\t", " "); // remove tabs, because json parser will fail here!
        if (!(str.startsWith(ARRAY_START)) && !str.startsWith(OBJECT_START)) {
          ret.append(QUOTE).append(key).append(QUOTE).append(COLON).append(QUOTE).append(value).append(QUOTE);
        } else {
          ret.append(QUOTE).append(key).append(QUOTE).append(COLON).append(value);
        }
      } else {
        ret.append(QUOTE).append(key).append(QUOTE).append(COLON).append(value);
      }
    } else {
      ret.append(QUOTE).append(key).append(QUOTE).append(COLON).append(value);
    }

    return ret.toString();
  }
}

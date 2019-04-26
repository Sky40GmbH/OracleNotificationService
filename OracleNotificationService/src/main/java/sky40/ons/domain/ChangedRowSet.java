package sky40.ons.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.util.Pair;

/**
 * Takes a set of changed rows. Container class. Adds utility functions.
 *
 * Extends {@link BaseObject} for serialization.
 *
 * Immutable type.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
public class ChangedRowSet extends BaseObject {

  /**
   * Maps the row to the list of values.
   */
  private final HashMap<String, List<String>> rowToValuesMap;

  /**
   * Maps the row to the list of operations.
   */
  private final HashMap<String, Set<String>> rowToOperationMap;

  /**
   * The list of column names (same for all rows)
   */
  private final List<String> columnNames;

  public ChangedRowSet(HashMap<String, List<String>> rowToValuesMap, List<String> columnNames, HashMap<String, Set<String>> rowToOperationMap) {
    this.rowToValuesMap = rowToValuesMap;
    this.columnNames = columnNames;
    this.rowToOperationMap = rowToOperationMap;
  }

  public List<String> getColumnNames() {
    return columnNames;
  }

  public HashMap<String, List<String>> getRowToValuesMap() {
    return rowToValuesMap;
  }

  /**
   * Convenience method to get column name and value of the row.
   *
   * @param rowId
   * @param column
   * @return
   */
  public Pair<String, String> findRowPair(String rowId, int column) {
    String key = columnNames.get(column);
    String value = rowToValuesMap.get(rowId).get(column);

    return new Pair<>(key, value);
  }

  /**
   *
   * @return number of rows in the result set.
   */
  public int rowSize() {
    return rowToValuesMap.size();
  }

  /**
   *
   * @return number of columns in the result set.
   */
  public int columnSize() {
    return columnNames.size();
  }

  @Override
  protected String toJson(boolean isEnclosed) {

    Iterator<String> rowIdIterator = rowToValuesMap.keySet().iterator();
    ArrayList<String> rowStrings = new ArrayList<>();
    // iterate rows
    for (int row = 0; row < rowSize(); row++) {
      String rowId = rowIdIterator.next();
      ArrayList<String> jsonPairs = new ArrayList<>();
      // iterate columns
      for (int col = 0; col < columnSize(); col++) {
        Pair<String, String> pair = findRowPair(rowId, col);
        String key = pair.getKey();
        if (!"rowid".equalsIgnoreCase(key)) {
          jsonPairs.add(jsonBuilder.pair(pair.getKey(), pair.getValue()));
        }
      }
      String rowStr
              = jsonBuilder.object(
                      jsonBuilder.pair("attributes", jsonBuilder.object(jsonPairs)),
                      jsonBuilder.pair("rowId", rowId),
                      jsonBuilder.pair("operations", jsonBuilder.array(true, rowToOperationMap.get(rowId)))
              );
      rowStrings.add(rowStr);
    }

    String ret = jsonBuilder.pair("rows", jsonBuilder.array(rowStrings));
    if (isEnclosed) {
      ret = jsonBuilder.object(ret);
    }
    return ret;
  }

}

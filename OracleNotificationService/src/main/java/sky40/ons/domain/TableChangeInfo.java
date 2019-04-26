package sky40.ons.domain;

import java.util.Date;
import oracle.jdbc.dcn.TableChangeDescription;

/**
 * Class to gather information on a changed table.
 *
 * Extends {@link BaseObject} for serialization.
 *
 * Immutable type.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
public class TableChangeInfo extends BaseObject {

  private final Date timestamp;
  private final TableChangeDescription desc;
  private final ChangedRowSet changedRows;

  public TableChangeInfo(Date timestamp, TableChangeDescription desc, ChangedRowSet changedRows) {
    this.timestamp = timestamp;
    this.desc = desc;
    this.changedRows = changedRows;
  }

  @Override
  protected String toJson(boolean isEnclosed) {
    String ret = jsonBuilder.chain(
            jsonBuilder.pair("table", desc.getTableName()),
            jsonBuilder.pair("operations", jsonBuilder.array(true, desc.getTableOperations())),
            jsonBuilder.pair("time", timestamp.getTime()),
            jsonBuilder.pair("rows", changedRows)
    );

    if (isEnclosed) {
      ret = jsonBuilder.object(ret);
    }
    return ret;
  }

}

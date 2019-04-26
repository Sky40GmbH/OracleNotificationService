package sky40.ons.listener;

import sky40.ons.domain.TableChangeInfo;
import sky40.ons.domain.ChangedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.RowChangeDescription;
import oracle.jdbc.dcn.TableChangeDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Oracle table change listener. Handles the change event that is being triggered by the database.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Service
@Slf4j
public class ONSchangeListener implements DatabaseChangeListener {

  private DatabaseManager manager;
  private ONSchangeNotifier notifier;

  @Autowired
  public void setManager(DatabaseManager dbm) {
    this.manager = dbm;
  }

  @Autowired
  public void setNotifier(ONSchangeNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public void onDatabaseChangeNotification(DatabaseChangeEvent dce) {
    log.info("Received database change event on listener " + dce.getRegId());
    if (dce.getRegId() == manager.getRegistrationId()) {
      log.info("Handle database change event : \n" + dce.toString());
      handleChangeEvent(dce);
    } else {
      log.info("Trying to remove orphaned registration with id "+dce.getRegId());
      manager.removeRegistration((int) dce.getRegId());
    }
  }

  private void handleChangeEvent(DatabaseChangeEvent dce) {
    Date timestamp = new Date();
    DatabaseChangeEvent.EventType event = dce.getEventType();

    switch (event) {
      case OBJCHANGE:
        TableChangeDescription[] changeDescriptions = dce.getTableChangeDescription();
        List<TableChangeInfo> tableChanges = new ArrayList<>();
        for (TableChangeDescription desc : changeDescriptions) {
          try {
            TableChangeInfo changeInfo = handleTableRowChanges(timestamp, desc);
            tableChanges.add(changeInfo);
            String message = changeInfo.toJson();
            notifier.notify(message);
          } catch (SQLException ex) {
            log.error("Error handling table rows: " + ex.getMessage());
          }
        }
        break;
      default:
        log.debug("DatabaseChangeEvent of EventType " + event.name() + " not handled.");
    }
  }

  /**
   * Handle the changes on table rows that were notified by the DB.
   *
   * @param timestamp time of notification event
   * @param desc Description of table changes by Oracle.
   */
  private TableChangeInfo handleTableRowChanges(Date timestamp, TableChangeDescription desc) throws SQLException {

    // 1.) collect row change information first
    String tableName = desc.getTableName();
    HashMap<String, Set<String>> changedRowMap = new HashMap<>();
    HashSet<String> deletedRows = new HashSet<>();
    RowChangeDescription[] rcds = desc.getRowChangeDescription();
    for (RowChangeDescription rcd : rcds) {
      String rowid = rcd.getRowid().stringValue();

      RowChangeDescription.RowOperation op = rcd.getRowOperation();
      switch (op) {
        case INSERT:
        // same as update: fall thru
        case UPDATE:
          Set<String> ops = changedRowMap.get(rowid);
          if (ops == null) {
            ops = new HashSet<>();
          }
          ops.add(op.toString());
          changedRowMap.put(rowid, ops);
          break;
        case DELETE:
          deletedRows.add(rowid);
        default:
        // do nothing
      }
    }

    // 2.) Query changed rows
    ChangedRowSet changedRows = manager.queryChanges(tableName, changedRowMap);
    TableChangeInfo tableChgInfo = new TableChangeInfo(timestamp, desc, changedRows);

    return tableChgInfo;
  }

}

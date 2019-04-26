package sky40.ons.listener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.NotificationRegistration;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.driver.OracleConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sky40.ons.ApplicationConfig;
import sky40.ons.domain.ChangedRowSet;

/**
 * The class that manages all operations with the database. Use as a singleton
 * by injecting as @autowired with spring.
 *
 * @author Hendrik Stilke {@literal (Hendrik.Stilke@sky40.de)}
 */
@Slf4j
@Component
public class DatabaseManager {

  DatabaseChangeRegistration reg = null;
  OracleConnection conn = null;

  @Autowired
  private ApplicationConfig config;

  public ApplicationConfig getConfig() {
    return config;
  }

  @Autowired
  public void setConfig(ApplicationConfig config) {
    this.config = config;
  }

  /**
   * Connect to database and register for changes to listen to. Configuration is
   * taken from application.properties file.
   *
   * @param listener The Change listener to call on changes.
   */
  public void startup(DatabaseChangeListener listener) {
    log.info("Database Manager startup");
    log.info("Connecting to database ...");

    try {
      conn = connectDatabase();
      reg = getNotificationRegistration(conn);
      if (reg != null) {
        log.info("Registered change notification with id " + reg.getRegId());
        NotificationRegistration.RegistrationState state = reg.getState();
        log.info("registration STATE is " + state);
        log.info("adding listener ...");
        reg.addListener(listener);
        registerTablesOnListener(conn, reg);
      }

    } catch (SQLException ex) {
      log.error("SQL error: " + ex);
    } catch (ClassNotFoundException ex2) {
      log.error("Driver initialization error: " + ex2);
    }
  }

  /**
   * Register the tables to listen changes for
   *
   * @param conn The databsse connection object.
   * @param reg The change registration handler object to attach to.
   * @throws SQLException
   */
  private void registerTablesOnListener(OracleConnection conn, DatabaseChangeRegistration reg) throws SQLException {
    // add objects in the registration:
    try (Statement stmt = conn.createStatement()) {
      OracleStatement ostmt = (OracleStatement) stmt;
      // associate the statement with the registration:
      ostmt.setDatabaseChangeRegistration(reg);

      for (String tableName : config.getNotification().getTableName()) {
        try (ResultSet rs = ostmt.executeQuery("SELECT * FROM " + tableName + " WHERE rownum<=1")) {
          while (rs.next()) {
          }
          String[] tableNames = reg.getTables();
          for (int i = 0; i < tableNames.length; i++) {
            log.info("table " + tableNames[i] + " will be listened for.");
          }
        }
      }

    }
  }

  /**
   * Connects the client to the database.
   *
   * @return the connection object
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private OracleConnection connectDatabase() throws SQLException, ClassNotFoundException {
    String dbURL = "jdbc:oracle:thin:@" + config.getOracle().getHostname() + ":" + config.getOracle().getPort() + ":" + config.getOracle().getDatabase(); // localhost:1521:XE";
    String username = config.getOracle().getUser(); // z.B. "ADMIN";
    String password = config.getOracle().getPass(); // z.B. "ISTRATOR" 

    Class.forName("oracle.jdbc.OracleDriver");
    Connection dbConn = DriverManager.getConnection(dbURL, username, password);
    if (dbConn != null) {
      log.info("... Database connected.");
    }

    if (dbConn instanceof OracleConnection) {
      OracleConnection oraConn = (OracleConnection) dbConn;
      log.info("Oracle connection was established.");
      return oraConn;
    }
    return null;
  }

  /**
   * Registers at the database for notifications and table changes.
   *
   * !Note: accessing is allowed if this was granted: GRANT EXECUTE ON
   * DBMS_CQ_NOTIFICATION to ADMIN and GRANT CHANGE NOTIFICATION to ADMIN
   *
   * @param oraConn The connection object to use.
   * @return The registration object.
   * @throws SQLException Is thrown in case of an invalid operation.
   */
  private DatabaseChangeRegistration getNotificationRegistration(OracleConnection oraConn) throws SQLException {
    DatabaseChangeRegistration ret = null;

    if (ret == null) // build a new registration
    {
      log.info("Building new change registration ...");

      // list of properties is found here:
      // https://docs.oracle.com/cd/E11882_01/java.112/e16548/dbchgnf.htm#JJDBC28815
      Properties properties = new Properties();

      properties.setProperty(OracleConnection.DCN_IGNORE_DELETEOP, "false");
      properties.setProperty(OracleConnection.DCN_IGNORE_INSERTOP, "false");
      properties.setProperty(OracleConnection.DCN_IGNORE_UPDATEOP, "false");
      properties.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
      properties.setProperty(OracleConnection.NTF_LOCAL_HOST, config.getNotification().getClientHostname());
      properties.setProperty(OracleConnection.NTF_LOCAL_TCP_PORT, config.getNotification().getClientPort() + "");

      ret = oraConn.registerDatabaseChangeNotification(properties);
    }
    return ret;
  }

  long getRegistrationId() {
    return (this.reg == null) ? -1 : this.reg.getRegId();
  }

  /**
   * Removes the listener registration from the DB.
   *
   * @param regId id of the registration
   */
  void removeRegistration(int regId) {
    try {
      log.info(this.reg.getRegistrationOptions().toString());
      this.conn.unregisterDatabaseChangeNotification(regId, config.getOracle().getHostname(), config.getOracle().getPort());
    } catch (SQLException ex) {
      log.error("Error unregistering registration with id " + regId + ":" + ex);
    }
  }

  /**
   * Query all changes (on a single table) at once and return the changed
   * entities.
   *
   * @param tableName Name of the table to query changes.
   * @param changedRowMap map of changed rows
   * @return map with changed rows
   * @throws SQLException
   */
  public ChangedRowSet queryChanges(String tableName, HashMap<String, Set<String>> changedRowMap) throws SQLException {

    String query = buildChangesQuery(tableName, changedRowMap.keySet());

    try (Statement stmt = conn.createStatement()) {
      OracleStatement ostmt = (OracleStatement) stmt;

      try (ResultSet rs = ostmt.executeQuery(query)) {
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        ArrayList<String> columns = new ArrayList<>();
        for (int col = 0; col < cols; col++) {
          String name = rsmd.getColumnName(col + 1);
          columns.add(name);
        }

        HashMap<String, List<String>> rows = new HashMap<>();

        while (rs.next()) {
          List<String> values = new ArrayList<>();
          for (int col = 0; col < cols; col++) {
            String value = rs.getString(col + 1);
            values.add(value);
          }
          rows.put(rs.getString("rowid"), values);
        }

        ChangedRowSet ret = new ChangedRowSet(rows, columns, changedRowMap);
        return ret;
      }
    }
  }

  /**
   * Build a query on changed rows for the table.
   *
   * @param tableName name of the table to query
   * @param changedRows map of the rows to query the changes for.
   * @return Db query
   */
  String buildChangesQuery(String tableName, Set<String> changedRows) {
    // like this: SELECT rowid, admin.aircraft.* FROM admin.aircraft WHERE rowid IN ('AAAE6zAAFAAAAJ7AAB','AAAE6zAAFAAAAJ7AAC');
    StringBuilder query = new StringBuilder("SELECT rowid, " + tableName + ".* FROM " + tableName + " WHERE rowid IN (");
    int i = 0;
    for (String id : changedRows) {
      if (i++ > 0) {
        query.append(", ");
      }
      query.append("'").append(id).append("'");
    }
    query.append(")");

    log.debug("query is : " + query.toString());
    return query.toString();

  }

}

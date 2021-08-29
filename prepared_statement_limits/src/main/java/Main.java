import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public final class Main {

  public static void main(String [] args) throws Exception {
    if (args.length != 5) {
      System.err.println("Main [mysql|postgres|derby] <hostname> <port> <username> <password>");
      System.exit(-1);
    }
    int i = 0;
    final String dbType = args[i++];
    final String hostname = args[i++];
    final String port = args[i++];
    final String username = args[i++];
    final String password = args[i++];
    final String url = "jdbc:" + dbType + "://" + hostname + ":" + port + "/";

    try(Connection conn = DriverManager.getConnection(url, username, password)) {
        Main main = new Main(conn);
        main.runExperiments();
    } 
  }

  private final Connection conn;

  private Main(Connection conn) {
    this.conn = conn;
  }

  private void runExperiments() throws Exception {
    System.out.println("Success?   Query");

    runExperiment(
      false,
      Collections.emptyList(),
      "CREATE DATABASE ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_db");
        pstmt.executeUpdate();
      },
      Collections.emptyList()
    );

    runExperiment(
      "DROP DATABASE ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_db");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE USER ? IDENTIFIED BY ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_user");
        pstmt.setString(2, "insecure password");
        pstmt.executeUpdate();
      },
      "DROP USER sample_user"
    );

    runExperiment(
      "CREATE USER sample_user IDENTIFIED BY 'insecure password'",
      "DROP USER ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_user");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE TABLE ? (sample_str VARCHAR(256), sample_int INT)",
      (pstmt) -> {
        pstmt.setString(1, "sample_table");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE TABLE sample_table (? VARCHAR(256), sample_int INT)",
      (pstmt) -> {
        pstmt.setString(1, "sample_str");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
      "DROP TABLE ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_table");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
      "INSERT INTO ? (sample_str, sample_int) values ('sample_value', 1)",
      (pstmt) -> {
        pstmt.setString(1, "sample_table");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
      "INSERT INTO sample_table (?, sample_int) values ('sample_value', 1)",
      (pstmt) -> {
        pstmt.setString(1, "sample_str");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
      "INSERT INTO sample_table (sample_str, sample_int) values (?, 1)",
      (pstmt) -> {
        pstmt.setString(1, "sample_value");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "DELETE FROM ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_table");
        pstmt.executeUpdate();
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "DELETE FROM sample_table WHERE ?='sample_value'",
      (pstmt) -> {
        pstmt.setString(1, "sample_str");
        pstmt.executeUpdate();
      },
      () -> {
        try (Statement stmt = conn.createStatement()) {
          try (ResultSet resultSet = stmt.executeQuery("SELECT count(*) FROM sample_table")) {
            if (!resultSet.next()) {
              return false;
            }
            return resultSet.getInt(0) == 0;
          }
        }
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "DELETE FROM sample_table WHERE sample_str=?",
      (pstmt) -> {
        pstmt.setString(1, "sample_value");
        pstmt.executeUpdate();
      },
      () -> {
        try (Statement stmt = conn.createStatement()) {
          try (ResultSet resultSet = stmt.executeQuery("SELECT count(*) FROM sample_table")) {
            if (!resultSet.next()) {
              return false;
            }
            return resultSet.getInt(0) == 0;
          }
        }
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "SELECT * FROM ?",
      (pstmt) -> {
        pstmt.setString(1, "sample_table");
        pstmt.executeQuery();
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "SELECT ? FROM sample_table",
      (pstmt) -> {
        pstmt.setString(1, "sample_str");
        try (ResultSet resultSet = pstmt.executeQuery()) {
          if (!resultSet.next()) {
            throw new IllegalStateException("Was expecting a single row");
          }
          if (!"sample_value".equals(resultSet.getString(1))) {
            throw new IllegalStateException("Was expecting a single row with 1st column equal to sample_value");
          }
        }
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "SELECT * FROM sample_table WHERE ?='sample_value'",
      (pstmt) -> {
        pstmt.setString(1, "sample_str");
        try (ResultSet resultSet = pstmt.executeQuery()) {
          if (!resultSet.next()) {
            throw new IllegalStateException("Was expecting a single row");
          }
          if (!"sample_value".equals(resultSet.getString(1))) {
            throw new IllegalStateException("Was expecting a single row with 1st column equal to sample_value");
          }
          if (resultSet.getInt(2) != 1) {
            throw new IllegalStateException("Was expecting a single row with 2nd column equal to 1");
          }
        }
      }
    );

    runExperiment(
      Arrays.asList(
        "CREATE TABLE sample_table (sample_str VARCHAR(256), sample_int INT)",
        "INSERT INTO sample_table (sample_str, sample_int) values ('sample_value', 1)"
      ),
      "SELECT * FROM sample_table WHERE sample_str=?",
      (pstmt) -> {
        pstmt.setString(1, "sample_value");
        try (ResultSet resultSet = pstmt.executeQuery()) {
          if (!resultSet.next()) {
            throw new IllegalStateException("Was expecting a single row");
          }
          if (!"sample_value".equals(resultSet.getString(1))) {
            throw new IllegalStateException("Was expecting a single row with 1st column equal to sample_value");
          }
          if (resultSet.getInt(2) != 1) {
            throw new IllegalStateException("Was expecting a single row with 2nd column equal to 1");
          }
        }
      }
    );
  }

  @FunctionalInterface
  public interface CheckedConsumer<T> {
     void apply(T t) throws Exception;
  }

  @FunctionalInterface
  public interface CheckedProducer<T> {
     T apply() throws Exception;
  }

  private void runExperiment(
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment
  ) throws Exception {
    runExperiment(
      true,
      Collections.emptyList(),
      preparedSqlString,
      experiment,
      () -> true,
      Collections.emptyList()
    );
  }

  private void runExperiment(
      String preReq,
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment
  ) throws Exception {
    runExperiment(
      true,
      Arrays.asList(preReq),
      preparedSqlString,
      experiment,
      () -> true,
      Collections.emptyList()
    );
  }

  private void runExperiment(
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment,
      String customCleanup
  ) throws Exception {
    runExperiment(
      true,
      Collections.emptyList(),
      preparedSqlString,
      experiment,
      () -> true,
      Arrays.asList(customCleanup)
    );
  }

  private void runExperiment(
      String preReq,
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment,
      String customCleanup
  ) throws Exception {
    runExperiment(
      true,
      Arrays.asList(preReq),
      preparedSqlString,
      experiment,
      () -> true,
      Arrays.asList(customCleanup)
    );
  }

  private void runExperiment(
      List<String> preReqs,
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment
  ) throws Exception {
    runExperiment(
      true,
      preReqs,
      preparedSqlString,
      experiment,
      () -> true,
      Collections.emptyList()
    );
  }

  private void runExperiment(
      List<String> preReqs,
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment,
      CheckedProducer<Boolean> verification
  ) throws Exception {
    runExperiment(
      true,
      preReqs,
      preparedSqlString,
      experiment,
      verification,
      Collections.emptyList()
    );
  }

  private void runExperiment(
      boolean createSampleDB,
      List<String> preReqs,
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment,
      List<String> customCleanup
  ) throws Exception {
    runExperiment(
      createSampleDB,
      preReqs,
      preparedSqlString,
      experiment,
      () -> true,
      customCleanup
    );
  }

  private void runExperiment(
      boolean createSampleDB,
      List<String> preReqs,
      String preparedSqlString,
      CheckedConsumer<PreparedStatement> experiment,
      CheckedProducer<Boolean> verification,
      List<String> customCleanup
  ) throws Exception {
    if (createSampleDB) {
      try (Statement statement = conn.createStatement()) {
        statement.executeUpdate("CREATE DATABASE sample_db");
      }
      try (Statement statement = conn.createStatement()) {
        statement.executeUpdate("USE sample_db");
      }
    }

    for (String preReqSql : preReqs) {
      try (Statement statement = conn.createStatement()) {
        statement.executeUpdate(preReqSql);
      }
    }
    
    try (PreparedStatement pstmt = conn.prepareStatement(preparedSqlString)) {
      experiment.apply(pstmt);
      if (verification.apply()) {
        System.out.print("✔");
      } else {
        System.out.print("✖");
      }
    } catch (Exception e) {
      System.out.print("✖");
    }
    System.out.println("          " + preparedSqlString);

    if (createSampleDB) {
      try (Statement statement = conn.createStatement()) {
        statement.executeUpdate("DROP DATABASE sample_db");
      }
    }

    for (String cleanupSql : customCleanup) {
      try (Statement statement = conn.createStatement()) {
        statement.executeUpdate(cleanupSql);
      }
    }
  }
  
}

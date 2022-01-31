import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Main {

  @Option(name="--experiment", usage="the type of experiment", required = true)
  private Experiment experiment;

  /**
   * Options for Files_lines,
   */
  @Option(name="--file", usage="the file to open", required = false)
  private File file;

  /**
   * Options for Files_list,
   */
  @Option(name="--directory", usage="the directory to list", required = false)
  private File directory;

  /**
   * Options for DB:
   *   java_sql_Statement,
   *   java_sql_PreparedStatement,
   *   java_sql_Connection,
   *   java_sql_ResultSet
   */
  @Option(name="--dbType",usage="Sets type of DB to connect to")
  public String dbType = "mysql";

  @Option(name="--hostname",usage="Sets hostname of the db to connect to")
  public String hostname = "localhost";

  @Option(name="--port",usage="Sets port of the db to connect to")
  public int port = 3306;

  @Option(name="--user",usage="The user to connect to the db with")
  public String username = "root";

  @Option(name="--pass",usage="The pass to connect to the db with")
  public String password = "password";

  public void run() throws Exception {
    switch (experiment) {
      case Files_lines:
        filesLinesLeak();
        break;
      case Files_walk:
        filesWalkLeak();
        break;
      case java_sql_Statement:
        javaSqlStatementLeak();
        break;
      case java_sql_PreparedStatement:
        javaSqlPreparedStatementLeak();
        break;
      case java_sql_Connection:
        javaSqlConnectionLeak();
        break;
      case java_sql_ResultSet:
        javaSqlResultSetLeak();
        break;
      default:
        throw new UnsupportedOperationException("Experiment " + experiment + " not implemented yet!");
    }
  }

  /**
   * java.nio.file.FileSystemException: pom.xml: Too many open files
   *     at sun.nio.fs.UnixException.translateToIOException (UnixException.java:100)
   *     at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:106)
   *     at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:111)
   *     at sun.nio.fs.UnixFileSystemProvider.newFileChannel (UnixFileSystemProvider.java:182)
   *     at java.nio.channels.FileChannel.open (FileChannel.java:292)
   *     at java.nio.channels.FileChannel.open (FileChannel.java:345)
   *     at java.nio.file.Files.lines (Files.java:4104)
   *     at java.nio.file.Files.lines (Files.java:4196)
   *     at Main.filesLinesLeak (Main.java:72)
   *     at Main.run (Main.java:63)
   *     at Main.main (Main.java:85)
   *     at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
   *     at java.lang.Thread.run (Thread.java:831)
   *
   * @throws IOException
   */
  private void filesLinesLeak() throws IOException {
    while (true) {
      try {
        Stream<String> lines = Files.lines(file.toPath());
        throw new IgnoredException("Does this cause a leak?");
      } catch (IgnoredException e) {
        // do nothing and keep leaking resources
      }
    }
  }

  /**
   * java.nio.file.FileSystemException: .: Too many open files
   *     at sun.nio.fs.UnixException.translateToIOException (UnixException.java:100)
   *     at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:106)
   *     at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:111)
   *     at sun.nio.fs.UnixFileSystemProvider.newDirectoryStream (UnixFileSystemProvider.java:419)
   *     at java.nio.file.Files.newDirectoryStream (Files.java:476)
   *     at java.nio.file.FileTreeWalker.visit (FileTreeWalker.java:300)
   *     at java.nio.file.FileTreeWalker.walk (FileTreeWalker.java:322)
   *     at java.nio.file.FileTreeIterator.<init> (FileTreeIterator.java:71)
   *     at java.nio.file.Files.walk (Files.java:3891)
   *     at java.nio.file.Files.walk (Files.java:3945)
   *     at Main.filesWalkLeak (Main.java:104)
   *     at Main.run (Main.java:66)
   *     at Main.main (Main.java:117)
   *     at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
   *     at java.lang.Thread.run (Thread.java:831)
   *
   * @throws IOException
   */
  private void filesWalkLeak() throws IOException {
    while (true) {
      try {
        Stream<Path> lines = Files.walk(directory.toPath());
        throw new IgnoredException("Does this cause a leak?");
      } catch (IgnoredException e) {
        // do nothing and keep leaking resources
      }
    }
  }

  /**
   * Nothing happened which I did not expect.
   *
   * @throws Exception
   */
  private void javaSqlStatementLeak() throws Exception {
    final String url = "jdbc:" + dbType + "://" + hostname + ":" + port + "/";
    try(Connection conn = DriverManager.getConnection(url, username, password)) {
      while (true) {
        try {
          Statement statement = conn.createStatement();
          throw new IgnoredException("Does this cause a leak?");
        } catch (IgnoredException e) {
          // do nothing and keep leaking resources
        }
      }
    }
  }

  /**
   * Nothing happened which I did not expect.
   *
   * @throws Exception
   */
  private void javaSqlPreparedStatementLeak() throws Exception {
    final String url = "jdbc:" + dbType + "://" + hostname + ":" + port + "/";
    try(Connection conn = DriverManager.getConnection(url, username, password)) {
      int i = 0;
      while (true) {
        try {
          PreparedStatement statement = conn.prepareStatement("select 1");
          statement.execute();
          throw new IgnoredException("Does this cause a leak?");
        } catch (IgnoredException e) {
          // do nothing and keep leaking resources
        }
      }
    }
  }

  /**
   * java.sql.SQLNonTransientConnectionException: Could not connect to HostAddress{host='localhost', port=3306, type='master'}. Too many connections
   *     at org.mariadb.jdbc.internal.util.exceptions.ExceptionFactory.createException (ExceptionFactory.java:73)
   *     at org.mariadb.jdbc.internal.util.exceptions.ExceptionFactory.create (ExceptionFactory.java:188)
   *     at org.mariadb.jdbc.internal.protocol.AbstractConnectProtocol.connectWithoutProxy (AbstractConnectProtocol.java:1402)
   *     at org.mariadb.jdbc.internal.util.Utils.retrieveProxy (Utils.java:635)
   *     at org.mariadb.jdbc.MariaDbConnection.newConnection (MariaDbConnection.java:150)
   *     at org.mariadb.jdbc.Driver.connect (Driver.java:89)
   *     at java.sql.DriverManager.getConnection (DriverManager.java:677)
   *     at java.sql.DriverManager.getConnection (DriverManager.java:228)
   *     at Main.javaSqlConnectionLeak (Main.java:181)
   *     at Main.run (Main.java:74)
   *     at Main.main (Main.java:194)
   *     at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
   *     at java.lang.Thread.run (Thread.java:831)
   * Caused by: java.sql.SQLException: Too many connections
   *     at org.mariadb.jdbc.internal.com.read.ReadInitialHandShakePacket.<init> (ReadInitialHandShakePacket.java:92)
   *     at org.mariadb.jdbc.internal.protocol.AbstractConnectProtocol.createConnection (AbstractConnectProtocol.java:527)
   *     at org.mariadb.jdbc.internal.protocol.AbstractConnectProtocol.connectWithoutProxy (AbstractConnectProtocol.java:1389)
   *     at org.mariadb.jdbc.internal.util.Utils.retrieveProxy (Utils.java:635)
   *     at org.mariadb.jdbc.MariaDbConnection.newConnection (MariaDbConnection.java:150)
   *     at org.mariadb.jdbc.Driver.connect (Driver.java:89)
   *     at java.sql.DriverManager.getConnection (DriverManager.java:677)
   *     at java.sql.DriverManager.getConnection (DriverManager.java:228)
   *     at Main.javaSqlConnectionLeak (Main.java:181)
   *     at Main.run (Main.java:74)
   *     at Main.main (Main.java:194)
   *     at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
   *     at java.lang.Thread.run (Thread.java:831)
   *
   * @throws Exception
   */
  private void javaSqlConnectionLeak() throws Exception {
    final String url = "jdbc:" + dbType + "://" + hostname + ":" + port + "/";

    while (true) {
      try {
        Connection conn = DriverManager.getConnection(url, username, password);
        throw new IgnoredException("Does this cause a leak?");
      } catch (IgnoredException e) {
        // do nothing and keep leaking resources
      }
    }
  }

  /**
   * Nothing happened which I did not expect.
   *
   * @throws Exception
   */
  private void javaSqlResultSetLeak() throws Exception {
    final String url = "jdbc:" + dbType + "://" + hostname + ":" + port + "/";
    try(Connection conn = DriverManager.getConnection(url, username, password)) {
      while (true) {
        try {
          Statement statement = conn.createStatement();
          statement.setFetchSize(1);
          ResultSet resultSet = statement.executeQuery("SELECT 1, 2"
                  + " UNION SELECT 'a', 'b'"
                  + " UNION SELECT '3', '4'");
          throw new IgnoredException("Does this cause a leak?");
        } catch (IgnoredException e) {
          // do nothing and keep leaking resources
        }
      }
    }
  }

  public static void main(String [] args) throws Exception {
    Main bean = new Main();
    CmdLineParser parser = new CmdLineParser(bean);
    try {
      parser.parseArgument(args);
      bean.run();
    } catch (CmdLineException e) {
      // handling of wrong arguments
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
    }

  }

}

enum Experiment {
  Files_lines,
  Files_walk,
  java_sql_Statement,
  java_sql_PreparedStatement,
  java_sql_Connection,
  java_sql_ResultSet
}

class IgnoredException extends Exception {

  IgnoredException(String s) {
    super(s);
  }
}
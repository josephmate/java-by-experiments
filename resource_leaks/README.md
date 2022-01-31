What happens if you forget to close a resource?

Resources you can forget to close:
1. Files.lines
2. Files.list
3. java.sql.Statement
4. java.sql.PreparedStatement
5. java.sql.Connection
6. java.sql.ResultSet
7. Url
8. Apache closeable http
9. Funny business with jooq's auto cleanup



<details>
<summary>Click to reveal the solution by experiment.</summary>

Files.lines:
```
java.nio.file.FileSystemException: pom.xml: Too many open files
    at sun.nio.fs.UnixException.translateToIOException (UnixException.java:100)
    at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:106)
    at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:111)
    at sun.nio.fs.UnixFileSystemProvider.newFileChannel (UnixFileSystemProvider.java:182)
    at java.nio.channels.FileChannel.open (FileChannel.java:292)
    at java.nio.channels.FileChannel.open (FileChannel.java:345)
    at java.nio.file.Files.lines (Files.java:4104)
    at java.nio.file.Files.lines (Files.java:4196)
    at Main.filesLinesLeak (Main.java:72)
    at Main.run (Main.java:63)
    at Main.main (Main.java:85)
    at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
    at java.lang.Thread.run (Thread.java:831)
```

Files.list
```
java.nio.file.FileSystemException: .: Too many open files
        at sun.nio.fs.UnixException.translateToIOException (UnixException.java:100)
        at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:106)
        at sun.nio.fs.UnixException.rethrowAsIOException (UnixException.java:111)
        at sun.nio.fs.UnixFileSystemProvider.newDirectoryStream (UnixFileSystemProvider.java:419)
        at java.nio.file.Files.newDirectoryStream (Files.java:476)
        at java.nio.file.FileTreeWalker.visit (FileTreeWalker.java:300)
        at java.nio.file.FileTreeWalker.walk (FileTreeWalker.java:322)
        at java.nio.file.FileTreeIterator.<init> (FileTreeIterator.java:71)
    at java.nio.file.Files.walk (Files.java:3891)
    at java.nio.file.Files.walk (Files.java:3945)
    at Main.filesWalkLeak (Main.java:104)
    at Main.run (Main.java:66)
    at Main.main (Main.java:117)
    at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
    at java.lang.Thread.run (Thread.java:831)
```

java.sql.Statement: Didn't see any issue.

java.sql.PreparedStatement: Didn't see any issue.

java.sql.Connection:
```
java.sql.SQLNonTransientConnectionException: Could not connect to HostAddress{host='localhost', port=3306, type='master'}. Too many connections
    at org.mariadb.jdbc.internal.util.exceptions.ExceptionFactory.createException (ExceptionFactory.java:73)
    at org.mariadb.jdbc.internal.util.exceptions.ExceptionFactory.create (ExceptionFactory.java:188)
    at org.mariadb.jdbc.internal.protocol.AbstractConnectProtocol.connectWithoutProxy (AbstractConnectProtocol.java:1402)
    at org.mariadb.jdbc.internal.util.Utils.retrieveProxy (Utils.java:635)
    at org.mariadb.jdbc.MariaDbConnection.newConnection (MariaDbConnection.java:150)
    at org.mariadb.jdbc.Driver.connect (Driver.java:89)
    at java.sql.DriverManager.getConnection (DriverManager.java:677)
    at java.sql.DriverManager.getConnection (DriverManager.java:228)
    at Main.javaSqlConnectionLeak (Main.java:181)
    at Main.run (Main.java:74)
    at Main.main (Main.java:194)
    at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
    at java.lang.Thread.run (Thread.java:831)
Caused by: java.sql.SQLException: Too many connections
    at org.mariadb.jdbc.internal.com.read.ReadInitialHandShakePacket.<init> (ReadInitialHandShakePacket.java:92)
    at org.mariadb.jdbc.internal.protocol.AbstractConnectProtocol.createConnection (AbstractConnectProtocol.java:527)
    at org.mariadb.jdbc.internal.protocol.AbstractConnectProtocol.connectWithoutProxy (AbstractConnectProtocol.java:1389)
    at org.mariadb.jdbc.internal.util.Utils.retrieveProxy (Utils.java:635)
    at org.mariadb.jdbc.MariaDbConnection.newConnection (MariaDbConnection.java:150)
    at org.mariadb.jdbc.Driver.connect (Driver.java:89)
    at java.sql.DriverManager.getConnection (DriverManager.java:677)
    at java.sql.DriverManager.getConnection (DriverManager.java:228)
    at Main.javaSqlConnectionLeak (Main.java:181)
    at Main.run (Main.java:74)
    at Main.main (Main.java:194)
    at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
    at java.lang.Thread.run (Thread.java:831)
```

java.sql.ResultSet: Didn't see any issue.

</details>

package util;

import java.sql.*;

public class DB {
  private static final String URL  =
    "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12797407?useSSL=true&requireSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
  private static final String USER = "sql12797407";
  private static final String PASS = "XdvqPW4iPA";

  static {
    try { Class.forName("com.mysql.cj.jdbc.Driver"); }
    catch (ClassNotFoundException e) { throw new RuntimeException(e); }
  }

  public static Connection get() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }
}

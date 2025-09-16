package util;

import java.sql.*;

public class DB {
  private static final String URL  =
    "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12798839";
  private static final String USER = "sql12798839";
  private static final String PASS = "uyv5x6hDg7";

  static {
    try { Class.forName("com.mysql.cj.jdbc.Driver"); }
    catch (ClassNotFoundException e) { throw new RuntimeException(e); }
  }

  public static Connection get() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }
}

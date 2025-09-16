package dao;

import model.User;
import util.DB;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserDao {
  private User map(ResultSet rs) throws SQLException {
    User u = new User();
    u.setId(rs.getInt("id"));
    u.setUsername(rs.getString("username"));
    u.setPasswordHash(rs.getString("password_hash"));
    u.setRole(rs.getString("role"));
    return u;
  }

  public User findByUsername(String username) throws SQLException {
    String sql = "SELECT * FROM users WHERE username=?";
    try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? map(rs) : null;
      }
    }
  }

  // NEW: id -> username
  public Map<Integer,String> nameMap() throws SQLException {
    try (Connection c = DB.get(); Statement st = c.createStatement()) {
      ResultSet rs = st.executeQuery("SELECT id, username FROM users");
      Map<Integer,String> m = new HashMap<>();
      while (rs.next()) m.put(rs.getInt(1), rs.getString(2));
      return m;
    }
  }
}

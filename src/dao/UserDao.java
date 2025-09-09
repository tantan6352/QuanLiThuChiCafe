package dao;

import model.User;
import util.DB;

import java.sql.*;

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
}

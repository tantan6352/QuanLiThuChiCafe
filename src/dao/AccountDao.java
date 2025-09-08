package dao;

import model.Account;
import util.DB;

import java.sql.*;
import java.util.*;

public class AccountDao {
  private Account map(ResultSet rs) throws SQLException {
    return new Account(
      rs.getInt("id"),
      rs.getString("name"),
      rs.getString("type"),
      rs.getBoolean("is_active")
    );
  }

  public List<Account> listActive() throws SQLException {
    String sql = "SELECT id,name,type,is_active FROM accounts WHERE is_active=1 ORDER BY name";
    try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
      ResultSet rs = ps.executeQuery();
      List<Account> list = new ArrayList<>();
      while (rs.next()) list.add(map(rs));
      return list;
    }
  }

  public Map<Integer,String> nameMap() throws SQLException {
    try (Connection c = DB.get(); Statement st = c.createStatement()) {
      ResultSet rs = st.executeQuery("SELECT id,name FROM accounts");
      Map<Integer,String> m = new HashMap<>();
      while (rs.next()) m.put(rs.getInt(1), rs.getString(2));
      return m;
    }
  }
}

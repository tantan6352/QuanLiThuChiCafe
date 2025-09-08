package dao;

import model.Category;
import model.TransactionType;
import util.DB;

import java.sql.*;
import java.util.*;

public class CategoryDao {
  private Category map(ResultSet rs) throws SQLException {
    return new Category(
      rs.getInt("id"),
      rs.getString("name"),
      TransactionType.valueOf(rs.getString("type"))
    );
  }

  public int create(String name, TransactionType type) throws SQLException {
    String sql = "INSERT INTO categories(name,type) VALUES(?,?)";
    try (Connection c = DB.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name.trim());
      ps.setString(2, type.name());
      ps.executeUpdate();
      try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
    }
  }

  public void rename(int id, String newName) throws SQLException {
    String sql = "UPDATE categories SET name=? WHERE id=?";
    try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, newName.trim());
      ps.setInt(2, id);
      ps.executeUpdate();
    }
  }

  public void delete(int id) throws SQLException {
    try (Connection c = DB.get();
         PreparedStatement ps = c.prepareStatement("DELETE FROM categories WHERE id=?")) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  public List<Category> listByType(TransactionType type) throws SQLException {
    String sql = "SELECT * FROM categories WHERE type=? ORDER BY name";
    try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, type.name());
      ResultSet rs = ps.executeQuery();
      List<Category> list = new ArrayList<>();
      while (rs.next()) list.add(map(rs));
      return list;
    }
  }

  public List<Category> listAll() throws SQLException {
    String sql = "SELECT * FROM categories ORDER BY type, name";
    try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
      ResultSet rs = ps.executeQuery();
      List<Category> list = new ArrayList<>();
      while (rs.next()) list.add(map(rs));
      return list;
    }
  }

  public Map<Integer,String> nameMap() throws SQLException {
    try (Connection c = DB.get(); Statement st = c.createStatement()) {
      ResultSet rs = st.executeQuery("SELECT id,name FROM categories");
      Map<Integer,String> m = new HashMap<>();
      while (rs.next()) m.put(rs.getInt(1), rs.getString(2));
      return m;
    }
  }

  public boolean exists(int id) throws SQLException {
    try (Connection c = DB.get();
         PreparedStatement ps = c.prepareStatement("SELECT 1 FROM categories WHERE id=?")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
    }
  }
}

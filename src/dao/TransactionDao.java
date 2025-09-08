package dao;

import model.Transaction;
import model.TransactionType;
import util.DB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ... phần thân giữ nguyên


public class TransactionDao {
  private Transaction map(ResultSet rs) throws SQLException {
    Transaction t = new Transaction();
    t.setId(rs.getLong("id"));
    t.setOccuredAt(rs.getTimestamp("occured_at").toLocalDateTime());
    t.setType(TransactionType.valueOf(rs.getString("type")));
    t.setCategoryId(rs.getInt("category_id"));
    t.setAccountId(rs.getInt("account_id"));
    t.setAmount(rs.getBigDecimal("amount"));
    t.setNote(rs.getString("note"));
    t.setCreatedBy((Integer) rs.getObject("created_by"));
    return t;
  }

  public long insert(Transaction t) throws SQLException {
    String sql = """
      INSERT INTO transactions(occured_at, type, category_id, account_id, amount, note, created_by)
      VALUES(?,?,?,?,?,?,?)""";
    try (Connection c = DB.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setTimestamp(1, Timestamp.valueOf(t.getOccuredAt()));
      ps.setString(2, t.getType().name());
      ps.setInt(3, t.getCategoryId());
      ps.setInt(4, t.getAccountId());
      ps.setBigDecimal(5, t.getAmount());
      ps.setString(6, t.getNote());
      if (t.getCreatedBy()==null) ps.setNull(7, Types.INTEGER); else ps.setInt(7, t.getCreatedBy());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getLong(1); }
    }
  }

  public List<Transaction> search(LocalDateTime from, LocalDateTime to,
                                  TransactionType type, Integer categoryId, Integer accountId,
                                  String keyword, int limit, int offset) throws SQLException {
    StringBuilder sb = new StringBuilder("""
      SELECT * FROM transactions WHERE occured_at BETWEEN ? AND ?""");
    if (type != null) sb.append(" AND type=?");
    if (categoryId != null) sb.append(" AND category_id=?");
    if (accountId != null) sb.append(" AND account_id=?");
    if (keyword != null && !keyword.isBlank()) sb.append(" AND note LIKE ?");
    sb.append(" ORDER BY occured_at DESC LIMIT ? OFFSET ?");

    try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sb.toString())) {
      int i=1;
      ps.setTimestamp(i++, Timestamp.valueOf(from));
      ps.setTimestamp(i++, Timestamp.valueOf(to));
      if (type!=null) ps.setString(i++, type.name());
      if (categoryId!=null) ps.setInt(i++, categoryId);
      if (accountId!=null) ps.setInt(i++, accountId);
      if (keyword!=null && !keyword.isBlank()) ps.setString(i++, "%"+keyword+"%");
      ps.setInt(i++, limit);
      ps.setInt(i, offset);
      ResultSet rs = ps.executeQuery();
      List<Transaction> list = new ArrayList<>();
      while (rs.next()) list.add(map(rs));
      return list;
    }
  }

  public void delete(long id) throws SQLException {
    try (Connection c = DB.get();
         PreparedStatement ps = c.prepareStatement("DELETE FROM transactions WHERE id=?")) {
      ps.setLong(1, id); ps.executeUpdate();
    }
  }
}


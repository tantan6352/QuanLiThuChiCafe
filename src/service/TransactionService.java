package service;

import dao.TransactionDao;
import model.Transaction;
import model.TransactionType;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionService {
  private final TransactionDao dao;

  public TransactionService(TransactionDao dao) { this.dao = dao; }

  public long addIncome(int categoryId, int accountId, BigDecimal amount, String note, Integer userId) throws SQLException {
    return add(TransactionType.INCOME, categoryId, accountId, amount, note, userId);
  }

  public long addExpense(int categoryId, int accountId, BigDecimal amount, String note, Integer userId) throws SQLException {
    return add(TransactionType.EXPENSE, categoryId, accountId, amount, note, userId);
  }

  private long add(TransactionType type, int categoryId, int accountId, BigDecimal amount, String note, Integer userId) throws SQLException {
    if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Số tiền phải > 0");
    Transaction t = new Transaction();
    t.setOccuredAt(LocalDateTime.now());
    t.setType(type);
    t.setCategoryId(categoryId);
    t.setAccountId(accountId);
    t.setAmount(amount);
    t.setNote(note);
    t.setCreatedBy(userId);
    return dao.insert(t);
  }

  public Map<String, BigDecimal> summaryMonth(LocalDate anyDayInMonth) throws SQLException {
    LocalDate first = anyDayInMonth.withDayOfMonth(1);
    LocalDate last = first.plusMonths(1).minusDays(1);
    var list = dao.search(first.atStartOfDay(), last.atTime(23,59,59), null, null, null, null, 10_000, 0);
    BigDecimal income = BigDecimal.ZERO, expense = BigDecimal.ZERO;
    for (var t : list) {
      if (t.getType() == TransactionType.INCOME) income = income.add(t.getAmount());
      else expense = expense.add(t.getAmount());
    }
    return Map.of(
      "income", income,
      "expense", expense,
      "profit", income.subtract(expense)
    );
  }
}

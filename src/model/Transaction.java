package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
  private long id;
  private LocalDateTime occuredAt;
  private TransactionType type;
  private int categoryId;
  private int accountId;
  private BigDecimal amount;
  private String note;
  private Integer createdBy;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }

  public LocalDateTime getOccuredAt() { return occuredAt; }
  public void setOccuredAt(LocalDateTime occuredAt) { this.occuredAt = occuredAt; }

  public TransactionType getType() { return type; }
  public void setType(TransactionType type) { this.type = type; }

  public int getCategoryId() { return categoryId; }
  public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

  public int getAccountId() { return accountId; }
  public void setAccountId(int accountId) { this.accountId = accountId; }

  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }

  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }

  public Integer getCreatedBy() { return createdBy; }
  public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
}

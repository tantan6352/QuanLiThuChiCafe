package model;

public class Account {
  private int id;
  private String name;
  private String type;   // "CASH" hoặc "BANK"
  private boolean active;

  public Account() {}
  public Account(int id, String name, String type, boolean active) {
    this.id = id; this.name = name; this.type = type; this.active = active;
  }

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  @Override public String toString() { return name; }
}

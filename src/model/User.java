package model;

public class User {
  private int id;
  private String username;
  private String passwordHash; // có thể là plain / SHA-256 / bcrypt
  private String role;         // ADMIN / STAFF

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
}


package service;

import dao.UserDao;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AuthService {
  private final UserDao userDao = new UserDao();

  public User login(String username, String password) throws Exception {
    if (username == null || username.isBlank() || password == null) {
      throw new IllegalArgumentException("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
    }
    User u = userDao.findByUsername(username.trim());
    if (u == null) throw new IllegalArgumentException("Sai tài khoản hoặc mật khẩu");

    String stored = u.getPasswordHash();
    if (matches(stored, password)) return u;

    throw new IllegalArgumentException("Sai tài khoản hoặc mật khẩu");
  }

  // Hỗ trợ 3 kiểu: bcrypt (nếu có jar), SHA-256, hoặc plain-text
  private boolean matches(String stored, String raw) throws Exception {
    if (stored == null) return false;

    // 1) Thử bcrypt bằng reflection (không cần compile jar)
    if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
      try {
        Class<?> cls = Class.forName("org.mindrot.jbcrypt.BCrypt");
        var m = cls.getMethod("checkpw", String.class, String.class);
        Object ok = m.invoke(null, raw, stored);
        return (Boolean) ok;
      } catch (Throwable ignore) {
        // Không có thư viện -> coi như không khớp
      }
    }

    // 2) So SHA-256 (nếu DB lưu hash SHA-256)
    if (stored.equalsIgnoreCase(sha256(raw))) return true;

    // 3) So plain-text
    return stored.equals(raw);
  }

  private String sha256(String s) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] b = md.digest(s.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder();
    for (byte x : b) sb.append(String.format("%02x", x));
    return sb.toString();
  }
}

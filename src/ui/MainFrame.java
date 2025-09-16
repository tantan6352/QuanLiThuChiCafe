package ui;

import javax.swing.*;
import ui.panel.TransactionPanel;
import ui.panel.CategoryPanel;
import ui.panel.ReportPanel;
import model.User;

public class MainFrame extends JFrame {
  private final User currentUser;

  public MainFrame(User user) {
    super("QuanLiThuChiCafe - " + (user!=null ? user.getUsername() + " (" + user.getRole() + ")" : ""));
    this.currentUser = user;
    initUI();
  }

  // Giữ constructor cũ (nếu cần chạy thẳng, không login)
  public MainFrame() { this(null); }

  private void initUI() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1100, 700);
    setLocationRelativeTo(null);

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Giao dịch", new TransactionPanel());
    tabs.addTab("Danh mục",  new CategoryPanel());
    tabs.addTab("Báo cáo",   new ReportPanel());
    setContentPane(tabs);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      // Mặc định: mở màn hình đăng nhập
      new LoginFrame().setVisible(true);
      // Nếu muốn bỏ đăng nhập: new MainFrame().setVisible(true);
    });
  }
}

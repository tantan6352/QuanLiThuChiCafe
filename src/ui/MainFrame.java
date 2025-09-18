package ui;

import javax.swing.*;
import ui.panel.TransactionPanel;
import ui.panel.CategoryPanel;
import ui.panel.ReportPanel;
import model.User;
//

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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1100, 700);
    setLocationRelativeTo(null);

    UiStyle.setWindowIcon(this, "/icons/coffee.png", 24, 24);  // icon cửa sổ

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Giao dịch",
        UiStyle.icon("/icons/txn.png",16,16), new ui.panel.TransactionPanel(currentUser));
    tabs.addTab("Danh mục",
        UiStyle.icon("/icons/category.png",16,16), new ui.panel.CategoryPanel());
    tabs.addTab("Báo cáo",
        UiStyle.icon("/icons/report.png",16,16), new ui.panel.ReportPanel());
    setContentPane(tabs);


  }

  public static void main(String[] args) {
  SwingUtilities.invokeLater(() -> {
    UiStyle.installSystemLaf();             // LAF hệ thống
    UiStyle.applyGlobalFont("Segoe UI", 12); // font toàn cục (tùy chọn)
    new LoginFrame().setVisible(true);
  });
}
}

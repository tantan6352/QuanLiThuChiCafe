package ui;

import javax.swing.*;
import ui.panel.TransactionPanel;
import ui.panel.CategoryPanel;
import ui.panel.ReportPanel;

public class MainFrame extends JFrame {
  public MainFrame() {
    super("QuanLiThuChiCafe");
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
    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
  }
}

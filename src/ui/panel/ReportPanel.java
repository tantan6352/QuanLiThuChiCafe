package ui.panel;

import dao.TransactionDao;
import dao.CategoryDao;
import dao.AccountDao;
import model.TransactionType;  // <— dùng import này

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

public class ReportPanel extends JPanel {
  private final TransactionDao tranDao = new TransactionDao();
  private final CategoryDao   catDao  = new CategoryDao();
  private final AccountDao    accDao  = new AccountDao();

  // Đổi tên model -> tblModel
  private final DefaultTableModel tblModel = new DefaultTableModel(
      new Object[]{"ID","Thời điểm","Loại","Danh mục","Tài khoản","Số tiền","Ghi chú"}, 0) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
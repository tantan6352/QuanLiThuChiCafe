package ui.panel;

import dao.TransactionDao;
import dao.CategoryDao;
import dao.AccountDao;
import dao.UserDao;
import model.TransactionType;
import ui.BackgroundPanel;
import ui.UiStyle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

public class ReportPanel extends BackgroundPanel {
  private final TransactionDao tranDao = new TransactionDao();
  private final CategoryDao   catDao  = new CategoryDao();
  private final AccountDao    accDao  = new AccountDao();

  private final DefaultTableModel tblModel = new DefaultTableModel(
      new Object[]{"ID","Thời điểm","Loại","Danh mục","Tài khoản","Số tiền","Ghi chú","Người nhập"}, 0) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
  };

  private JTextField tfFrom, tfTo, tfKeyword;
  private JComboBox<String> cbType;

  public ReportPanel() {
    super("/icons/report.png"); // ẢNH NỀN
    setLayout(new BorderLayout(8,8));

    // Filter bar (trong suốt)
    JPanel bar = new JPanel(new GridBagLayout());
    bar.setOpaque(false);
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(4,6,4,6); g.anchor = GridBagConstraints.WEST;

    tfFrom = new JTextField(10); tfTo = new JTextField(10);
    tfFrom.setText(LocalDate.now().withDayOfMonth(1).toString());
    tfTo.setText(LocalDate.now().toString());
    cbType = new JComboBox<>(new String[]{"Tất cả","Thu","Chi"});
    tfKeyword = new JTextField(18);
    JButton btnSearch = new JButton("Lọc");
    JButton btnExport = new JButton("Xuất CSV");

    g.gridx=0; g.gridy=0; bar.add(new JLabel("Từ (Năm-Tháng-Ngày):"), g);
    g.gridx=1; bar.add(tfFrom, g);
    g.gridx=2; bar.add(new JLabel("Đến:"), g);
    g.gridx=3; bar.add(tfTo, g);
    g.gridx=4; bar.add(new JLabel("Loại:"), g);
    g.gridx=5; bar.add(cbType, g);
    g.gridx=6; bar.add(new JLabel("Từ khóa:"), g);
    g.gridx=7; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL; bar.add(tfKeyword, g);
    g.gridx=8; g.weightx=0; g.fill=GridBagConstraints.NONE; bar.add(btnSearch, g);
    g.gridx=9; bar.add(btnExport, g);

    add(bar, BorderLayout.NORTH);

    JTable table = new JTable(tblModel);
    UiStyle.prettyTable(table);
    UiStyle.centerHeader(table);

    JScrollPane sp = new JScrollPane(table);
    sp.setOpaque(false);
    sp.getViewport().setOpaque(false);
    add(sp, BorderLayout.CENTER);

    btnSearch.addActionListener(e -> reload());
    btnExport.addActionListener(e -> exportCsv());

    reload();
  }

  private void reload() {
    try {
      LocalDate from = LocalDate.parse(tfFrom.getText().trim());
      LocalDate to   = LocalDate.parse(tfTo.getText().trim());
      String kw = tfKeyword.getText().trim();
      String sel = (String) cbType.getSelectedItem();

      tblModel.setRowCount(0);
      Map<Integer,String> catMap  = catDao.nameMap();
      Map<Integer,String> accMap  = accDao.nameMap();
      Map<Integer,String> userMap = new UserDao().nameMap();

      TransactionType typeFilter =
          "Thu".equals(sel) ? TransactionType.INCOME :
          "Chi".equals(sel) ? TransactionType.EXPENSE : null;

      var list = tranDao.search(
          from.atStartOfDay(),
          to.atTime(23,59,59),
          typeFilter,
          null, null,
          kw.isEmpty() ? null : kw, 5000, 0);

      for (var t : list) {
        String createdByName = (t.getCreatedBy()==null) ? "" :
            userMap.getOrDefault(t.getCreatedBy(), String.valueOf(t.getCreatedBy()));
        tblModel.addRow(new Object[]{
          t.getId(), t.getOccuredAt(), t.getType(),
          catMap.getOrDefault(t.getCategoryId(), String.valueOf(t.getCategoryId())),
          accMap.getOrDefault(t.getAccountId(), String.valueOf(t.getAccountId())),
          t.getAmount(), t.getNote(), createdByName
        });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void exportCsv() {
    try {
      if (tblModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất."); return; }
      JFileChooser fc = new JFileChooser();
      fc.setSelectedFile(new java.io.File("bao_cao.csv"));
      if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

      try (var fw = new FileWriter(fc.getSelectedFile(), StandardCharsets.UTF_8)) {
        fw.write("ID,Ngay gio,Loai,Danh muc,Tai khoan,So tien,Ghi chu,Nguoi nhap\n");
        for (int r=0; r<tblModel.getRowCount(); r++) {
          StringBuilder line = new StringBuilder();
          for (int c=0; c<tblModel.getColumnCount(); c++) {
            String v = String.valueOf(tblModel.getValueAt(r, c)).replace("\"","\"\"");
            if (v.contains(",") || v.contains("\"") ) v = "\"" + v + "\"";
            line.append(v);
            if (c < tblModel.getColumnCount()-1) line.append(',');
          }
          line.append('\n');
          fw.write(line.toString());
        }
      }
      JOptionPane.showMessageDialog(this, "Đã xuất CSV!");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Xuất CSV lỗi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}

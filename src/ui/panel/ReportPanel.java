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
  };

  private JTextField tfFrom, tfTo, tfKeyword;
  private JComboBox<String> cbType;

  public ReportPanel() {
    setLayout(new BorderLayout(8,8));

    // Filter bar
    JPanel bar = new JPanel(new GridBagLayout());
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(4,6,4,6); g.anchor = GridBagConstraints.WEST;

    tfFrom = new JTextField(10); tfTo = new JTextField(10);
    tfFrom.setText(LocalDate.now().withDayOfMonth(1).toString());
    tfTo.setText(LocalDate.now().toString());
    cbType = new JComboBox<>(new String[]{"ALL","INCOME","EXPENSE"});
    tfKeyword = new JTextField(18);
    JButton btnSearch = new JButton("Lọc");
    JButton btnExport = new JButton("Xuất CSV");

    g.gridx=0; g.gridy=0; bar.add(new JLabel("Từ (yyyy-MM-dd):"), g);
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
    table.setRowHeight(22);
    add(new JScrollPane(table), BorderLayout.CENTER);

    btnSearch.addActionListener(e -> reload());
    btnExport.addActionListener(e -> exportCsv());

    reload(); // initial
  }

  private void reload() {
    try {
      LocalDate from = LocalDate.parse(tfFrom.getText().trim());
      LocalDate to   = LocalDate.parse(tfTo.getText().trim());
      String kw = tfKeyword.getText().trim();
      String sel = (String) cbType.getSelectedItem();

      tblModel.setRowCount(0);

      Map<Integer,String> catMap = catDao.nameMap();
      Map<Integer,String> accMap = accDao.nameMap();

      TransactionType typeFilter =
          "INCOME".equals(sel) ? TransactionType.INCOME :
          "EXPENSE".equals(sel) ? TransactionType.EXPENSE : null;

      var list = tranDao.search(
          from.atStartOfDay(),
          to.atTime(23,59,59),
          typeFilter,
          null, null,
          kw.isEmpty() ? null : kw, 5000, 0);

      for (var t : list) {
        tblModel.addRow(new Object[]{
          t.getId(),
          t.getOccuredAt(),
          t.getType(),
          catMap.getOrDefault(t.getCategoryId(), String.valueOf(t.getCategoryId())),
          accMap.getOrDefault(t.getAccountId(), String.valueOf(t.getAccountId())),
          t.getAmount(),
          t.getNote()
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
        fw.write("ID,Ngay gio,Loai,Danh muc,Tai khoan,So tien,Ghi chu\n");
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

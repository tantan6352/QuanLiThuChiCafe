package ui.panel;

import dao.CategoryDao;
import model.Category;
import model.TransactionType;
import ui.BackgroundPanel;
import ui.UiStyle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CategoryPanel extends BackgroundPanel {
  private final CategoryDao dao = new CategoryDao();
  private final DefaultTableModel model = new DefaultTableModel(
      new Object[]{"ID","Tên danh mục","Loại"}, 0) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
  };

  private JComboBox<TransactionType> cbType;
  private JTextField tfName;
  private JTable table;

  public CategoryPanel() {
    super("/icons/category.png"); // ẢNH NỀN
    setLayout(new BorderLayout(8,8));

    // Form (trong suốt để thấy nền)
    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8,8));
    form.setOpaque(false);

    cbType = new JComboBox<>(TransactionType.values());
    tfName = new JTextField(20);
    JButton btnAdd = new JButton("Thêm");
    JButton btnRename = new JButton("Đổi tên");
    JButton btnDelete = new JButton("Xóa");
  

    form.add(new JLabel("Loại:")); form.add(cbType);
    form.add(new JLabel("Tên:"));  form.add(tfName);
    form.add(btnAdd); form.add(btnRename); form.add(btnDelete);
    add(form, BorderLayout.NORTH);

    // Table
    table = new JTable(model);
    UiStyle.prettyTable(table);
    UiStyle.centerHeader(table);

    JScrollPane sp = new JScrollPane(table);
    sp.setOpaque(false);
    sp.getViewport().setOpaque(false);
    add(sp, BorderLayout.CENTER);

    // Events
    btnAdd.addActionListener(e -> onAdd());
    btnRename.addActionListener(e -> onRename());
    btnDelete.addActionListener(e -> onDelete());

    reload();
  }

  private void onAdd() {
    try {
      String name = tfName.getText().trim();
      if (name.isEmpty()) throw new IllegalArgumentException("Chưa nhập tên danh mục");
      dao.create(name, (TransactionType) cbType.getSelectedItem());
      tfName.setText("");
      reload();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void onRename() {
    int row = table.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để đổi tên."); return; }
    int id = (int) model.getValueAt(row, 0);
    String current = (String) model.getValueAt(row, 1);
    String newName = JOptionPane.showInputDialog(this, "Tên mới:", current);
    if (newName == null || newName.trim().isEmpty()) return;
    try {
      dao.rename(id, newName.trim());
      reload();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void onDelete() {
    int row = table.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa."); return; }
    int id = (int) model.getValueAt(row, 0);
    if (JOptionPane.showConfirmDialog(this, "Xóa danh mục #" + id + " ?", "Xác nhận",
        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
    try {
      dao.delete(id);
      reload();
    } catch (SQLIntegrityConstraintViolationException fk) {
      JOptionPane.showMessageDialog(this, "Không thể xóa: danh mục đang dùng trong giao dịch.", "Ràng buộc dữ liệu", JOptionPane.WARNING_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void reload() {
    try {
      List<Category> list = dao.listAll();
      model.setRowCount(0);
      for (Category c : list) {
        model.addRow(new Object[]{ c.getId(), c.getName(), c.getType() });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Không tải được danh mục: " + e.getMessage());
    }
  }
}

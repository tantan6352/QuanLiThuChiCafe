package ui.panel;

import dao.TransactionDao;
import dao.CategoryDao;
import dao.AccountDao;
import dao.UserDao;
import model.TransactionType;
import model.Category;
import model.Account;
import model.User;
import service.TransactionService;
import ui.BackgroundPanel;
import ui.UiStyle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class TransactionPanel extends BackgroundPanel {
  private final TransactionService service = new TransactionService(new TransactionDao());
  private final CategoryDao categoryDao = new CategoryDao();
  private final AccountDao  accountDao  = new AccountDao();
  private final User currentUser;

  private final DefaultTableModel model = new DefaultTableModel(
      new Object[]{"ID","Thời điểm","Loại","Danh mục","Tài khoản","Số tiền","Ghi chú","Người nhập"}, 0) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
  };

  private JComboBox<TransactionType> cbType;
  private JComboBox<Category> cbCat;
  private JComboBox<Account> cbAcc;
  private JTextField tfAmt, tfNote;
  private JLabel lbIncome, lbExpense, lbProfit;

  public TransactionPanel(User u) {
    super("/icons/txn.png"); // ẢNH NỀN
    this.currentUser = u;
    initUI();
  }
  public TransactionPanel() { this(null); }

  private void initUI() {
    setLayout(new BorderLayout(8,8));

    // Form nhập (trong suốt)
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4,8,4,8);
    gbc.anchor = GridBagConstraints.WEST;

    cbType = new JComboBox<>(TransactionType.values());
    cbCat  = new JComboBox<>();
    cbAcc  = new JComboBox<>();
    tfAmt  = new JTextField(12);
    tfNote = new JTextField(26);
    JButton btnAdd = new JButton("Thêm");

    // Row 0
    gbc.gridy=0; gbc.gridx=0; form.add(new JLabel("Loại:"), gbc);
    gbc.gridx=1; gbc.weightx=0.25; gbc.fill=GridBagConstraints.HORIZONTAL; form.add(cbType, gbc);
    gbc.gridx=2; gbc.weightx=0; gbc.fill=GridBagConstraints.NONE; form.add(new JLabel("Danh mục:"), gbc);
    gbc.gridx=3; gbc.weightx=0.6; gbc.fill=GridBagConstraints.HORIZONTAL; form.add(cbCat, gbc);
    gbc.gridx=4; gbc.weightx=0; gbc.fill=GridBagConstraints.NONE; form.add(new JLabel("Tài khoản:"), gbc);
    gbc.gridx=5; gbc.weightx=0.6; gbc.fill=GridBagConstraints.HORIZONTAL; form.add(cbAcc, gbc);

    // Row 1
    gbc.gridy=1; gbc.gridx=0; form.add(new JLabel("Số tiền:"), gbc);
    gbc.gridx=1; gbc.weightx=0.25; gbc.fill=GridBagConstraints.HORIZONTAL; form.add(tfAmt, gbc);
    tfAmt.setHorizontalAlignment(SwingConstants.RIGHT); // tiền căn phải
    gbc.gridx=2; gbc.weightx=0; gbc.fill=GridBagConstraints.NONE; form.add(new JLabel("Ghi chú:"), gbc);
    gbc.gridx=3; gbc.gridwidth=2; gbc.weightx=1.0; gbc.fill=GridBagConstraints.HORIZONTAL; form.add(tfNote, gbc);
    gbc.gridx=5; gbc.gridwidth=1; gbc.weightx=0; gbc.fill=GridBagConstraints.NONE; gbc.anchor=GridBagConstraints.EAST;
    form.add(btnAdd, gbc);

    add(form, BorderLayout.NORTH);

    // Bảng
    JTable table = new JTable(model);
    UiStyle.prettyTable(table);
    UiStyle.centerHeader(table);

    JScrollPane sp = new JScrollPane(table);
    sp.setOpaque(false);
    sp.getViewport().setOpaque(false);
    add(sp, BorderLayout.CENTER);

    // Tổng hợp (trong suốt)
    JPanel pnlSum = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    pnlSum.setOpaque(false);
    lbIncome = new JLabel("Thu: 0");
    lbExpense= new JLabel("Chi: 0");
    lbProfit = new JLabel("Lãi: 0");
    pnlSum.add(lbIncome); pnlSum.add(new JLabel(" | "));
    pnlSum.add(lbExpense); pnlSum.add(new JLabel(" | "));
    pnlSum.add(lbProfit);
    add(pnlSum, BorderLayout.SOUTH);

    cbType.addActionListener(e -> loadCategories());
    btnAdd.addActionListener(e -> onAdd());

    loadAccounts();
    loadCategories();
    reloadTable();
    loadSummary();
  }

  private void onAdd() {
    try {
      var type = (TransactionType) cbType.getSelectedItem();
      var cat  = (Category) cbCat.getSelectedItem();
      var acc  = (Account)  cbAcc.getSelectedItem();
      if (cat == null) throw new IllegalArgumentException("Chưa chọn danh mục");
      if (acc == null) throw new IllegalArgumentException("Chưa chọn tài khoản");

      BigDecimal amt = new BigDecimal(tfAmt.getText().trim());
      if (amt.signum() <= 0) throw new IllegalArgumentException("Số tiền phải > 0");

      Integer uid = (currentUser != null) ? currentUser.getId() : null;

      long id = (type==TransactionType.INCOME)
          ? service.addIncome(cat.getId(), acc.getId(), amt, tfNote.getText(), uid)
          : service.addExpense(cat.getId(), acc.getId(), amt, tfNote.getText(), uid);

      JOptionPane.showMessageDialog(this, "Đã thêm giao dịch #" + id);
      tfAmt.setText(""); tfNote.setText("");
      reloadTable(); loadSummary();
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void loadAccounts() {
    try {
      cbAcc.removeAllItems();
      for (Account a : accountDao.listActive()) cbAcc.addItem(a);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Không tải được Tài khoản: " + e.getMessage());
    }
  }

  private void loadCategories() {
    try {
      cbCat.removeAllItems();
      var type = (TransactionType) cbType.getSelectedItem();
      for (Category c : categoryDao.listByType(type)) cbCat.addItem(c);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Không tải được Danh mục: " + e.getMessage());
    }
  }

  private void loadSummary() {
    try {
      var s = service.summaryMonth(LocalDate.now());
      lbIncome.setText("Thu: " + s.get("income"));
      lbExpense.setText("Chi: " + s.get("expense"));
      lbProfit.setText("Lãi: " + s.get("profit"));
    } catch (Exception ignored) { }
  }

  private void reloadTable() {
    try {
      model.setRowCount(0);
      Map<Integer,String> catMap  = categoryDao.nameMap();
      Map<Integer,String> accMap  = accountDao.nameMap();
      Map<Integer,String> userMap = new UserDao().nameMap();

      var list = new TransactionDao().search(
          LocalDate.now().withDayOfMonth(1).atStartOfDay(),
          LocalDate.now().atTime(23,59,59),
          null, null, null, null, 1000, 0);

      for (var t : list) {
        String createdByName = (t.getCreatedBy()==null) ? "" :
            userMap.getOrDefault(t.getCreatedBy(), String.valueOf(t.getCreatedBy()));
        model.addRow(new Object[]{
          t.getId(), t.getOccuredAt(), t.getType(),
          catMap.getOrDefault(t.getCategoryId(), String.valueOf(t.getCategoryId())),
          accMap.getOrDefault(t.getAccountId(), String.valueOf(t.getAccountId())),
          t.getAmount(), t.getNote(), createdByName
        });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Không tải được dữ liệu: " + e.getMessage());
    }
  }
}

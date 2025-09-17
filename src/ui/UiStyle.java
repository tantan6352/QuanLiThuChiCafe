package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public final class UiStyle {

    public static void leftColumns(JTable table, int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  private UiStyle() {}

  public static void installSystemLaf() {
    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
  }
  public static void applyGlobalFont(String family, int size) {
    var keys = UIManager.getDefaults().keys();
    var base = new Font(family, Font.PLAIN, size);
    while (keys.hasMoreElements()) {
      var k = keys.nextElement(); var v = UIManager.get(k);
      if (v instanceof Font) UIManager.put(k, base);
    }
  }

  /* Buttons */
  public static void beautifyButton(JButton b, Color bg) {
    b.setBackground(bg); b.setForeground(Color.WHITE);
    b.setFocusPainted(false);
    b.setBorder(new EmptyBorder(6,12,6,12));
    b.setOpaque(true); b.setContentAreaFilled(true);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  /* Table styling + zebra */
  public static void prettyTable(JTable t) {
    t.setRowHeight(22);
    t.setFillsViewportHeight(true);
    t.setGridColor(new Color(235,235,235));
    t.setSelectionBackground(new Color(230,240,255));
    t.setSelectionForeground(Color.BLACK);

    DefaultTableCellRenderer hdr =
        (DefaultTableCellRenderer) t.getTableHeader().getDefaultRenderer();
    hdr.setHorizontalAlignment(SwingConstants.CENTER);
    t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(Font.BOLD));

    DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
      @Override public Component getTableCellRendererComponent(
          JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248,248,248));
        return c;
      }
    };
    for (int i = 0; i < t.getColumnCount(); i++) {
      t.getColumnModel().getColumn(i).setCellRenderer(zebra);
    }
  }

  public static void centerHeader(JTable t) {
    DefaultTableCellRenderer hdr =
        (DefaultTableCellRenderer) t.getTableHeader().getDefaultRenderer();
    hdr.setHorizontalAlignment(SwingConstants.CENTER);
  }
  public static void centerAllColumns(JTable t) {
    DefaultTableCellRenderer r = new DefaultTableCellRenderer();
    r.setHorizontalAlignment(SwingConstants.CENTER);
    t.setDefaultRenderer(Object.class, r);
  }
  public static void rightColumns(JTable t, int... cols) {
    DefaultTableCellRenderer r = new DefaultTableCellRenderer();
    r.setHorizontalAlignment(SwingConstants.RIGHT);
    for (int c : cols) t.getColumnModel().getColumn(c).setCellRenderer(r);
  }

  /* Icons */
  public static ImageIcon icon(String classpath, int w, int h) {
    var url = UiStyle.class.getResource(classpath);
    if (url == null) return null;
    var img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    return new ImageIcon(img);
  }
  public static void setWindowIcon(Window w, String classpath, int wpx, int hpx) {
    var ic = icon(classpath, wpx, hpx);
    if (ic != null) w.setIconImage(ic.getImage());
  }
}

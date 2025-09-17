package ui;

import javax.swing.*;
import java.awt.*;

/** JPanel vẽ ảnh nền scale full khung */
public class BackgroundPanel extends JPanel {
  private final Image bg;

  /** @param classpath ví dụ: "/icons/bg.jpg" */
  public BackgroundPanel(String classpath) {
    var url = getClass().getResource(classpath);
    this.bg = (url != null) ? new ImageIcon(url).getImage() : null;
    setLayout(new BorderLayout());
  }

  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
  }
}

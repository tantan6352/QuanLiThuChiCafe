// src/util/BgPanel.java
package util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BgPanel extends JPanel {
  private final Image img;
  private float alpha = 1f; // 1.0 = đậm, 0.0 = trong suốt

  public BgPanel(String resourcePath) {
    URL u = getClass().getResource(resourcePath);
    if (u == null) throw new IllegalArgumentException("Không tìm thấy ảnh: " + resourcePath);
    this.img = new ImageIcon(u).getImage();
    setLayout(new BorderLayout()); // để add form vào giữa
  }

  public void setAlpha(float a) {
    this.alpha = Math.max(0f, Math.min(1f, a));
    repaint();
  }

  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (img == null) return;
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this); // scale full khung
    g2.dispose();
  }
}


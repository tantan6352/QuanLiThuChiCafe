package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import util.BgPanel;
import util.BgPanel;

public class LoginFrame extends JFrame {
  private final JTextField tfUser = new JTextField(18);
  private final JPasswordField pfPass = new JPasswordField(18);
  private final JButton btnLogin = new JButton("Đăng nhập");
  private final AuthService auth = new AuthService();

  public LoginFrame() {
    super("Đăng nhập - QuanLiThuChiCafe");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(380, 200);
    setLocationRelativeTo(null);

    setIconImage(new ImageIcon(getClass().getResource("/icons/coffee.png")).getImage());

    // NỀN
    BgPanel bg = new BgPanel("/icons/baner.png");
    bg.setAlpha(0.5f);
    setContentPane(bg);


    // 3) Form nội dung
    JPanel p = new JPanel(new GridBagLayout());
    p.setOpaque(false); // QUAN TRỌNG: để thấy ảnh nền
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(6,8,6,8); g.anchor = GridBagConstraints.WEST;

    g.gridx=0; g.gridy=0; p.add(new JLabel("Tài khoản:"), g);
    g.gridx=1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx=1; p.add(tfUser, g);
    g.gridx=0; g.gridy=1; g.fill = GridBagConstraints.NONE; g.weightx=0; p.add(new JLabel("Mật khẩu:"), g);
    g.gridx=1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx=1; p.add(pfPass, g);
    g.gridx=1; g.gridy=2; g.fill = GridBagConstraints.NONE; g.anchor=GridBagConstraints.EAST; p.add(btnLogin, g);

    bg.add(p, BorderLayout.CENTER);

    btnLogin.addActionListener(e -> doLogin());
    getRootPane().setDefaultButton(btnLogin);
  }

  private void doLogin() {
    try {
      String u = tfUser.getText().trim();
      String p = new String(pfPass.getPassword());
      User user = auth.login(u, p);
      new MainFrame(user).setVisible(true);
      dispose();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Đăng nhập thất bại: " + ex.getMessage(),
          "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
  }
}

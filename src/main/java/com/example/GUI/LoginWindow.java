package com.example.GUI;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JDialog {
    String username;

    public LoginWindow() {

        setTitle("Login");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModal(true);

        ImageIcon backgroundIcon = new ImageIcon("src/main/java/com/example/GUI/images/bg1.jpeg");
        Image backgroundImage = backgroundIcon.getImage();

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints c = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(0, 124, 161));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(10, 10, 10, 5);
        usernameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        backgroundPanel.add(usernameLabel, c);

        JTextField usernameField = new JTextField(5);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 10);
        usernameField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        usernameField.setBackground(new Color(2, 32, 42));
        usernameField.setForeground(Color.WHITE);
        backgroundPanel.add(usernameField, c);

        JButton continueButton = new JButton("Continue");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        continueButton.setFont(new Font("Monospaced", Font.PLAIN, 15));
        continueButton.setBackground(new Color(2, 32, 42));
        continueButton.setForeground(Color.WHITE);
        backgroundPanel.add(continueButton, c);

        continueButton.addActionListener(
                e -> {
                    String nick = usernameField.getText().trim();
                    if (nick.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Username cannot be empty", "Error",  JOptionPane.ERROR_MESSAGE);
                    } else {
                        username = nick;
                        dispose();
                    }
                }
        );

        getRootPane().setDefaultButton(continueButton);
    }

    public String getUsername() {
        return username;
    }
}

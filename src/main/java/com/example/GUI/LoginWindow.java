package com.example.GUI;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JDialog {
    String username;

    public LoginWindow() {

        setTitle("Login");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(10, 10, 10, 5);
        add(usernameLabel, c);

        JTextField usernameField = new JTextField(15);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 10);
        add(usernameField, c);

        JButton continueButton = new JButton("Continue");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        add(continueButton, c);

        continueButton.addActionListener(
                e->{
                    String nick = usernameField.getText().trim();
                    if(nick.isEmpty())
                    {
                        JOptionPane.showMessageDialog(this, "Username cannot be empty");
                    }
                    else
                    {
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

package com.example.GUI;

import org.apache.kafka.common.protocol.types.Field;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class RoomWindow extends JFrame {
    private String username;
    private final JPanel roomPanel = new JPanel();

    public RoomWindow() {
        login();
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login cancelled. Closing application.");
            dispose();
            System.exit(0);
            return;
        }
        new RoomWindow(username);
    }

    public RoomWindow(String username) {
        this.username = username;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login cancelled. Closing application.");
            dispose();
            System.exit(0);
            return;
        }

        setTitle(username + " chatroom");

        roomPanel.setLayout(new FlowLayout());
        List<String> rooms = Arrays.asList("General", "Java", "Cats");
        for (String room : rooms) {
            addRoomButton(room);
        }

        add(roomPanel, BorderLayout.CENTER);
        setVisible(true);
    }




    private void login()
    {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
        username = loginWindow.getUsername();
    }

    private void addRoomButton (String name)
    {
        JButton roomButton = new JButton(name);
        roomButton.setPreferredSize(new Dimension(100, 100));
        roomButton.addActionListener(e -> openChat(name));
        roomPanel.add(roomButton);
    }

    private void openChat(String room)
    {
        dispose();
        new ChatWindow(username, room);
    }

}

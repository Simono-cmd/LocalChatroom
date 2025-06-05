package com.example.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoomWindow extends JFrame {
    private static final String ROOMS_FILE = "src/main/java/com/example/GUI/rooms.txt";
    private String username;
    private final JPanel roomPanel = new JPanel();
    private List<String> rooms = new ArrayList<>();

    public RoomWindow() {
        login();
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login cancelled. Closing application.");
            dispose();
            System.exit(0);
            return;
        }
        initializeUI();
    }

    public RoomWindow(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        ImageIcon backgroundIcon = new ImageIcon("src/main/java/com/example/GUI/images/bg2.jpeg");
        Image backgroundImage = backgroundIcon.getImage();

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setTitle(username + " chatroom");

        roomPanel.setOpaque(false);
        roomPanel.setLayout(new FlowLayout());
        roomPanel.setBorder(new EmptyBorder(60, 40, 40, 40));
        rooms = loadRoomsFromFile();
        if (rooms.isEmpty()) {
            rooms = new ArrayList<>(Arrays.asList("General", "Java", "Test"));
        }

        for (String room : rooms) {
            addRoomButton(room);
        }

        backgroundPanel.add(roomPanel, BorderLayout.CENTER);

        // Dolny pasek z polami i przyciskami
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);

        PlaceholderTextField newRoomField = new PlaceholderTextField();
        newRoomField.setPlaceholder("Enter room name");
        newRoomField.setPreferredSize(new Dimension(200, 30));
        newRoomField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        newRoomField.setBackground(new Color(2, 32, 42));
        newRoomField.setForeground(Color.WHITE);

        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        addRoomButton.setBackground(new Color(2, 32, 42));
        addRoomButton.setForeground(Color.WHITE);
        getRootPane().setDefaultButton(addRoomButton);

        addRoomButton.addActionListener(e -> {
            String roomName = newRoomField.getText().trim();

            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (rooms.contains(roomName)) {
                JOptionPane.showMessageDialog(this, "Room with this name already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            rooms.add(roomName);
            addRoomButton(roomName);
            roomPanel.revalidate();
            roomPanel.repaint();
            newRoomField.setText("");
            saveRoomsToFile();
        });

        bottomPanel.add(newRoomField);
        bottomPanel.add(addRoomButton);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomWrapper.add(bottomPanel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        logoutButton.setBackground(new Color(2, 32, 42));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(94, 0, 0));
        logoutButton.addActionListener(e -> {
            dispose();
            new RoomWindow(); // wraca do loginu
        });

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        bottomWrapper.add(logoutPanel, BorderLayout.EAST);

        backgroundPanel.add(bottomWrapper, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void login() {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
        username = loginWindow.getUsername();
    }

    private void addRoomButton(String name) {
        JButton roomButton = new JButton(name);
        Dimension fixedSize = new Dimension(150, 150);
        roomButton.setPreferredSize(fixedSize);
        roomButton.setMinimumSize(fixedSize);
        roomButton.setMaximumSize(fixedSize);
        roomButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        roomButton.setBackground(new Color(2, 32, 42));
        roomButton.setForeground(Color.WHITE);
        roomButton.setBorder(new LineBorder(Color.WHITE));

        roomPanel.add(roomButton);

        roomButton.addActionListener(e -> openChat(name));

        roomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int confirm = JOptionPane.showConfirmDialog(
                            roomPanel,
                            "Do you want to delete the room \"" + name + "\"?",
                            "Delete Room",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        roomPanel.remove(roomButton);
                        rooms.remove(name);
                        roomPanel.revalidate();
                        roomPanel.repaint();
                        saveRoomsToFile();
                    }
                }
            }
        });
    }

    private void openChat(String room) {
        dispose();
        String kafkaTopic = room.replaceAll("\\s+", "_");
        new ChatWindow(username, kafkaTopic);
    }

    private List<String> loadRoomsFromFile() {
        try {
            Path path = Paths.get(ROOMS_FILE);
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            return Files.lines(path)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveRoomsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (String room : rooms) {
                writer.write(room);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

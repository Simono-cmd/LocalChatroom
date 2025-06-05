package com.example.GUI;

import com.example.Kafka.ChatConsumer;
import com.example.Kafka.ChatProducer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.Executors;

public class ChatWindow extends JFrame {
    private final JTextArea chatArea = new JTextArea();
    private final PlaceholderTextField inputField = new PlaceholderTextField();
    private final JButton sendButton = new JButton("Send");
    private final JButton leaveButton = new JButton("Leave");
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    public static final String GREEN = "\033[0;32m";
    public static final String RESET = "\033[0m";

    private final ChatProducer producer;
    private final String topic;
    private final String username;

    public ChatWindow(String username, String topic) {
        this.username = username;
        this.topic = topic;
        this.producer = new ChatProducer("localhost:9092");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setTitle("Room: " + topic);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("src/main/java/com/example/GUI/images/bg3.jpg");
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

        setupUI();
        startListening();
        producer.sendMessage(topic, "JOIN::" + username + "::");

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                leaveRoom();
            }
        });
    }


    private void setupUI() {
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setForeground(Color.WHITE);
        chatArea.setBackground(new Color(2, 32, 42));
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setOpaque(false);
        chatScroll.getViewport().setOpaque(false);

        inputField.setPlaceholder("Enter your message here");
        inputField.setBackground(new Color(2, 32, 42));
        inputField.setForeground(Color.WHITE);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));

        sendButton.setBackground(new Color(2, 32, 42));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        sendButton.setPreferredSize(new Dimension(100, 70));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setPreferredSize(new Dimension(0, 70));

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setOpaque(false);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value.toString().equals(username)) {
                    label.setForeground(Color.GREEN);
                } else {
                    label.setForeground(Color.WHITE);
                }
                if (isSelected) {
                    label.setBackground(new Color(40, 60, 80)); // Kolor zaznaczonego elementu
                } else {
                    label.setBackground(new Color(2, 32, 42));  // Kolor tła listy
                }
                label.setOpaque(true);
                return label;
            }
        });
        userList.setBackground(new Color(2, 32, 42));


        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setOpaque(false);
        userScroll.getViewport().setOpaque(false);

        JLabel userLabel = new JLabel("Active users:", SwingConstants.CENTER);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));

        leaveButton.setPreferredSize(new Dimension(200, 70));
        leaveButton.setBackground(new Color(99, 12, 12));
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        userPanel.setPreferredSize(new Dimension(200, 0));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(userScroll, BorderLayout.CENTER);
        userPanel.add(leaveButton, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);
        add(userPanel, BorderLayout.EAST);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        leaveButton.addActionListener(e -> leaveRoom());
    }



    private void sendMessage()
    {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String message = "MSG::" + username + "::" + text;
            producer.sendMessage(topic, message);
            inputField.setText("");
        }
    }

    private void startListening()
    {
        String groupID = username + "-" + System.currentTimeMillis();
        ChatConsumer consumer = new ChatConsumer("localhost:9092", topic, groupID);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                consumer.listen((message) -> {
                    SwingUtilities.invokeLater(() -> {
                        if (message.startsWith("MSG::")) {
                            String[] parts = message.split("::", 3);
                            String user = parts[1];
                            if (!userListModel.contains(user)) {
                                if(Objects.equals(user, username))
                                {
                                    chatArea.append(GREEN+user + ": " + message+RESET);
                                }
                                else
                                {
                                    chatArea.append(user + ": " + message);
                                }
                            }
                            chatArea.append(parts[1] + ": " + parts[2] + "\n");
                        } else if (message.startsWith("JOIN::")) {
                            String[] parts = message.split("::", 3);
                            String user = parts[1];
                            if (!userListModel.contains(user)) {
                                userListModel.addElement(user);
                            }
                            chatArea.append("-=- " + user + " joined -=-\n");


                        } else if (message.startsWith("LEAVE::")) {
                            String[] parts = message.split("::", 3);
                            String user = parts[1];
                            if (userListModel.contains(user)) {
                                userListModel.removeElement(user);
                                chatArea.append("-=- " + user + " left -=-" + "\n");
                            }
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void leaveRoom() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Do you really wanna leave the room?",
                "Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            producer.sendMessage(topic, "LEAVE::" + username + "::");
            dispose();
            new RoomWindow(username);
        }
    }



}

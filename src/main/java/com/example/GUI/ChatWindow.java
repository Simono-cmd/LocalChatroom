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
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");
    private final JButton leaveButton = new JButton("Leave");
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    private final JPanel bottomPanel = new JPanel();
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

        setTitle("Chatroom: " + topic + " - User: " + username);
        setSize(800, 600);
        setLayout(new BorderLayout());


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
        JScrollPane chatScroll = new JScrollPane(chatArea);
        add(chatScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setPreferredSize(new Dimension(600, 60));

        userListModel.addElement(username);
        JScrollPane userScroll = new JScrollPane(userList);
        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value.toString().equals(username)) {
                    c.setForeground(Color.GREEN);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        userScroll.setPreferredSize(new Dimension(100, 0));
        add(userScroll, BorderLayout.EAST);

        leaveButton.setPreferredSize(new Dimension(100, 60));

        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(leaveButton, BorderLayout.WEST);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        leaveButton.addActionListener(e -> leaveRoom());

    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String message = "MSG::" + username + "::" + text;
            producer.sendMessage(topic, message);
            inputField.setText("");
        }
    }

    private void startListening() {
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
                "Czy na pewno chcesz opuścić pokój?",
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

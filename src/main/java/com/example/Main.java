package com.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nickname: ");
        String nickname = scanner.nextLine();

        System.out.print("Room (topic): ");
        String topic = scanner.nextLine();

        ChatProducer producer = new ChatProducer("localhost:9092");
        ChatConsumer consumer = new ChatConsumer("localhost:9092", topic, nickname + "-group");

        new Thread(consumer::listen).start();

        while (true) {
            String msg = scanner.nextLine();
            producer.sendMessage(topic, nickname + ": " + msg);

        }
    }
}

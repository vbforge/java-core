package com.vbforge.practice.practice_2_synchronized_shared_log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Two client threads post messages into one shared chat log guarded by a synchronized method.
 * Lines are sorted before printing because thread interleaving is not deterministic.
 */
public class ChatRoomLogDemo {

    public static void main(String[] args) throws InterruptedException {
        ChatRoom chatRoom = new ChatRoom();
        Thread t1 = new Thread(new Client("Alice", chatRoom));
        Thread t2 = new Thread(new Client("Bob", chatRoom));
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        List<String> lines = new ArrayList<>(chatRoom.getChatLog().lines().toList());
        Collections.sort(lines);
        System.out.println("Chat Log (" + lines.size() + " messages, sorted):");
        lines.forEach(System.out::println);
    }

    static class ChatRoom {
        // The synchronized method already guards the log; StringBuffer's own locking is redundant here.
        private final StringBuffer chatLog = new StringBuffer();

        synchronized void addMessage(String message, String sender) {
            chatLog.append(sender + ": " + message + "\n");
        }

        String getChatLog() {
            return chatLog.toString();
        }
    }

    static class Client implements Runnable {
        private final String name;
        private final ChatRoom chatRoom;

        Client(String name, ChatRoom chatRoom) {
            this.name = name;
            this.chatRoom = chatRoom;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                chatRoom.addMessage("Message " + (i + 1) + " from " + name, name);
                try {
                    Thread.sleep(10); // was 200 ms; shortened, only needed to let the threads interleave
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}

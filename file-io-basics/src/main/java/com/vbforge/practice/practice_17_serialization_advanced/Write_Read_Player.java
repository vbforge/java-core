package com.vbforge.practice.practice_17_serialization_advanced;

import java.io.*;

public class Write_Read_Player {
    public static void main(String[] args) {

        writePlayer();
        readPlayer();

    }

    private static void readPlayer() {
        try {
            FileInputStream fis = new FileInputStream("file-io-basics/data_files/player.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Player player = (Player) ois.readObject();
            System.out.println(player);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writePlayer() {
        try {
            FileOutputStream fos = new FileOutputStream("file-io-basics/data_files/player.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Player player = new Player(1, "Recoba", Team.INTER, 20);
            oos.writeObject(player);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
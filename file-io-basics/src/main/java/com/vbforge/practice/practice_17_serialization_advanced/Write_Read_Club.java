package com.vbforge.practice.practice_17_serialization_advanced;

import java.io.*;

public class Write_Read_Club {
    public static void main(String[] args) {

        writeClubData();
        readClubData();

    }

    private static void readClubData() {
        try {
            FileInputStream fis = new FileInputStream("file-io-basics/data_files/clubs.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Club club = (Club) ois.readObject();
            System.out.println(club);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeClubData() {
        try {
            FileOutputStream fos = new FileOutputStream("file-io-basics/data_files/clubs.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Club club = new Club(1, "Inter", 24);
            oos.writeObject(club);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
package com.vbforge.practice.practice_17_serialization_advanced;

import java.io.*;

public class Write_Read_Vehicle {
    public static void main(String[] args) {

        writeVehicle();
        readVehicle();

    }

    private static void readVehicle() {
        try (FileInputStream fis = new FileInputStream("file-io-basics/data_files/vehicle.bin");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Vehicle vehicle = (Vehicle) ois.readObject();
            System.out.println(vehicle);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeVehicle() {
        try (FileOutputStream fos = new FileOutputStream("file-io-basics/data_files/vehicle.bin");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            Vehicle vehicle = new Vehicle(1, "Toyota", 50000.0);
            oos.writeObject(vehicle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
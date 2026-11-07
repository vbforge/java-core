package com.vbforge.practice.practice_6_single_object;

import java.io.*;

public class DataHelper {

    //write single Car
    public static void writeCar(File file, Car car) {

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(car);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    //read single Car
    public static Car readCar(File file) {

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Car) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
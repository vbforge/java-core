package com.vbforge.practice.practice_6_single_object;

import java.io.File;

public class Main {

    private static final File file = new File("file-io-basics/data_files/car1.txt");

    public static void main(String[] args) {

        //generate data: one Car instance
        Car car = DataGenerator.generateOneCar();

        //write one object to disk
        DataHelper.writeCar(file, car);

        //read object back from disk
        Car restored = DataHelper.readCar(file);

        //print restored object
        System.out.println(restored);

    }
}
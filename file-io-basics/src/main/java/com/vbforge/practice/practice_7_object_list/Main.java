package com.vbforge.practice.practice_7_object_list;

import com.vbforge.practice.practice_6_single_object.Car;

import java.io.File;
import java.util.List;

public class Main {

    private static final File fileGermanyAuto = new File("file-io-basics/data_files/AutosalonGermany.txt");
    private static final File fileJapanAuto   = new File("file-io-basics/data_files/AutosalonJapan.txt");

    public static void main(String[] args) {

        //prepare data
        List<Car> carsGermany = DataGenerator.generateCarsGermany();
        List<Car> carsJapan   = DataGenerator.generateCarsJapan();

        //prepare Autosalon:
        Autosalon autosalonGermany = DataGenerator.generateAutosalon(carsGermany, "Germany-Autosalon");
        Autosalon autosalonJapan   = DataGenerator.generateAutosalon(carsJapan,   "Japan-Autosalon");

        //write data method
        DataHelper.writeData(fileGermanyAuto, autosalonGermany);
        DataHelper.writeData(fileJapanAuto,   autosalonJapan);

        //read data method:
        Autosalon autosalonGermanyResult = DataHelper.readData(fileGermanyAuto);
        System.out.println(autosalonGermanyResult);
        System.out.println("========");
        Autosalon autosalonJapanResult = DataHelper.readData(fileJapanAuto);
        System.out.println(autosalonJapanResult);

    }
}
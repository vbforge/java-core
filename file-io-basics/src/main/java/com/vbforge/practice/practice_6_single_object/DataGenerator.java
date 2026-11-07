package com.vbforge.practice.practice_6_single_object;

import static com.vbforge.practice.practice_6_single_object.Country.*;
import static com.vbforge.practice.practice_6_single_object.Type.*;

public class DataGenerator {

    public static Car generateOneCar() {
        return new Car(1, "BMW", SUV, GERMANY);
    }

}
package com.vbforge.practice.practice_12_simple_field_validator;

/**
 * Field validation with @MyValidator + ValidatorEngine.
 *   1. CarF with price -10 fails with "price must be positive"
 *   2. CarF with price 10 passes
 * The failure is caught and printed so the following scene still runs.
 */
public class FieldValidatorDemo {

    public static void main(String[] args) {
        ValidatorEngine validatorEngine = new ValidatorEngine();
        scene1InvalidPrice(validatorEngine);
        scene2ValidPrice(validatorEngine);
    }

    static void scene1InvalidPrice(ValidatorEngine validatorEngine) {
        System.out.println("=== Scene 1: invalid price ===");
        CarF car = new CarF();
        car.setName("Car1");
        car.setPrice(-10);
        System.out.println(car);

        try {
            validatorEngine.validate(car);
        } catch (RuntimeException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }
    }

    static void scene2ValidPrice(ValidatorEngine validatorEngine) {
        System.out.println("\n=== Scene 2: valid price ===");
        CarF car = new CarF();
        car.setName("Car2");
        car.setPrice(10);
        validatorEngine.validate(car);
        System.out.println(car); //no error message
    }
}

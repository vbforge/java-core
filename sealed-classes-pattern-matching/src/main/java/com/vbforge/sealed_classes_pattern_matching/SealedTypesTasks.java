package com.vbforge.sealed_classes_pattern_matching;

/**
 * 🎯 TASKS — Sealed types
 *
 * <p>Implement every method so that {@code SealedTypesTasksTest} passes.
 * Remove the {@code @Disabled} annotation from one nested test class at a time.
 * Do NOT change method signatures.
 */
public class SealedTypesTasks {

    /**
     * Name a shape for display.
     *
     * <p>Examples:
     * <pre>
     *   shapeName(new Circle(1.0))     → "circle"
     *   shapeName(new Square(2.0))     → "square"
     *   shapeName(new Polygon(3, 4.5)) → "polygon with 3 sides"
     *   shapeName(new Hexagon(10.0))   → "polygon with 6 sides"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use a {@code switch} expression with type patterns</li>
     *   <li>Do NOT write a {@code default} branch — let the compiler prove completeness</li>
     * </ul>
     *
     * @param shape any shape
     * @return the display name as shown above
     */
    public String shapeName(SealedTypesDemo.Shape shape) {
        throw new UnsupportedOperationException("TODO: implement shapeName");
    }

    /**
     * Describe how a vehicle is powered.
     *
     * <p>Examples:
     * <pre>
     *   vehicleKind(new ElectricCar()) → "electric"
     *   vehicleKind(new PetrolCar())   → "petrol"
     *   vehicleKind(new Bicycle())     → "human-powered"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The hierarchy has two levels — look at what a switch must cover to be exhaustive</li>
     *   <li>No {@code default} branch</li>
     * </ul>
     *
     * @param vehicle any vehicle
     * @return the power source as shown above
     */
    public String vehicleKind(SealedTypesDemo.Vehicle vehicle) {
        throw new UnsupportedOperationException("TODO: implement vehicleKind");
    }

    /**
     * Count the leaf types of a hierarchy. A leaf is a type that is not {@code sealed}
     * (so {@code final} and {@code non-sealed} types are leaves). A type that is not
     * sealed at all counts as exactly one leaf.
     *
     * <p>Examples:
     * <pre>
     *   countLeafTypes(Shape.class)    → 3   (Circle, Square, Polygon)
     *   countLeafTypes(Vehicle.class)  → 3   (ElectricCar, PetrolCar, Bicycle)
     *   countLeafTypes(Car.class)      → 2   (ElectricCar, PetrolCar)
     *   countLeafTypes(Circle.class)   → 1
     *   countLeafTypes(String.class)   → 1
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Class#isSealed} and {@code Class#getPermittedSubclasses}</li>
     *   <li>The hierarchy is a tree — think recursion</li>
     * </ul>
     *
     * @param type the root of the hierarchy
     * @return number of leaf types
     */
    public int countLeafTypes(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement countLeafTypes");
    }
}

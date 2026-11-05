package com.vbforge.practice.practice_5_equals_with_canonical_form;

/**
 * equals() across different representations of the same quantity: 100 C == 212 F == 373.15 K.
 * Both equals and hashCode work on ONE canonical value (Celsius rounded to 1e-9), so
 * equal temperatures always share a hash and equality stays transitive.
 */
public class TemperatureEqualityDemo {

    static final class Temperature {
        private final double value;
        private final String unit;   // "C", "F", "K"
        private final long canonicalNanoCelsius;

        Temperature(double value, String unit) {
            if (!unit.equals("C") && !unit.equals("F") && !unit.equals("K"))
                throw new IllegalArgumentException("Unknown unit: " + unit);
            this.value = value;
            this.unit  = unit;
            this.canonicalNanoCelsius = Math.round(toCelsius() * 1e9);
        }

        private double toCelsius() {
            return switch (unit) {
                case "C" -> value;
                case "F" -> (value - 32) * 5.0 / 9.0;
                case "K" -> value - 273.15;
                default  -> throw new IllegalStateException();
            };
        }

        @Override public String toString() { return unit.equals("K") ? value + " K" : value + "°" + unit; }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Temperature t)) return false;
            return canonicalNanoCelsius == t.canonicalNanoCelsius;
        }

        @Override public int hashCode() {
            return Long.hashCode(canonicalNanoCelsius);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- equals() across units ---");
        Temperature c = new Temperature(100,    "C");
        Temperature f = new Temperature(212,    "F");
        Temperature k = new Temperature(373.15, "K");
        System.out.println("100°C = " + c + ", 212°F = " + f + ", 373.15 K = " + k);
        System.out.println("100°C equals 212°F:   " + c.equals(f));
        System.out.println("100°C equals 373.15 K: " + c.equals(k));

        System.out.println("\n--- hashCode contract ---");
        System.out.println("100°C hashCode: " + c.hashCode());
        System.out.println("212°F hashCode: " + f.hashCode());
        System.out.println("equal temps, equal hash: " + (c.hashCode() == f.hashCode()));
    }
}

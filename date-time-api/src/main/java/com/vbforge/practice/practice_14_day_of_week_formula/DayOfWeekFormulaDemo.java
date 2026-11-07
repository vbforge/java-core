package com.vbforge.practice.practice_14_day_of_week_formula;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implement the program that determines the day of the week for a given date;
 * Steps to resolve task:
 * <p>
 * 1) Parse the command-line arguments to get the month, day, and year.
 * 2) Apply the formulas provided to compute y0, x, m0, and d0.
 * 3) Output the result, which is d0, representing the day of the week.
 * <p>
 * Run modes (merged from DefineDayProgram / DayWeek / DayWeekSwing):
 *   - three arguments (month day year) → console output
 *   - no arguments                     → simple Swing UI with three inputs
 */
public class DayOfWeekFormulaDemo {

    /*Day of the week. Write a program that takes a date as input and prints the
    day of the week that date falls on. Your program should accept three int command-line
    arguments: m (month), d (day), and y (year). For m, use 1 for January, 2 for
    February, and so forth. For output, print 0 for Sunday, 1 for Monday, 2 for Tuesday,
    and so forth. Use the following formulas, for the Gregorian calendar:
    y0 = y - (14 - m) / 12
    x = y0 + y0 / 4 - y0 / 100 + y0 / 400
    m0 = m + 12 * ((14 - m) / 12) - 2
    d0 = (d + x + (31 * m0) / 12) % 7
    Example: On which day of the week did February 14, 2000 fall?
    y0 = 2000 - 1 = 1999
    x = 1999 + 1999 / 4 - 1999 / 100 + 1999 / 400 = 2483
    m0 = 2 + 12 * 1 - 2 = 12
    d0 = (14 + 2483 + (31 * 12) / 12) % 7 = 2500 % 7 = 1
    Answer : Monday.*/
    public static void main(String[] args) {
        if (args.length == 3) {
            runConsole(args);
        } else if (args.length == 0) {
            runSwing();
        } else {
            System.out.println("Please provide three arguments: month, day, year.");
        }
    }

    // ---------- shared formula ----------

    private static int calculateD0(int m, int d, int y) {
        // Calculate the variables as per the formulas
        int y0 = y - (14 - m) / 12;
        int x = y0 + y0 / 4 - y0 / 100 + y0 / 400;
        int m0 = m + 12 * ((14 - m) / 12) - 2;
        return (d + x + (31 * m0) / 12) % 7;
    }

    private static DaysOfTheWeek findDay(int d0) {
        DaysOfTheWeek[] days = DaysOfTheWeek.values();
        for (DaysOfTheWeek day : days) {
            if (d0 == day.value) {
                return day;
            }
        }
        return null;
    }

    // ---------- console mode (DefineDayProgram / DayWeek) ----------

    private static void runConsole(String[] args) {
        int m = Integer.parseInt(args[0]);
        int d = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);

        int d0 = calculateD0(m, d, y);

        // Output the day of the week
        DaysOfTheWeek day = findDay(d0);
        if (day != null) {
            System.out.println(day.name());
        }
//        System.out.println(d0);
        //for the 1 15 2025
        //program result WEDNESDAY
    }

    // ---------- Swing mode (DayWeekSwing) ----------

    private static void runSwing() {
        // Create the frame
        JFrame frame = new JFrame("Day of the Week Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(5, 2));

        // Input fields
        JTextField monthField = new JTextField();
        JTextField dayField = new JTextField();
        JTextField yearField = new JTextField();
        JLabel resultLabel = new JLabel("Result: ");
        JButton calculateButton = new JButton("Calculate");

        // Add components to the frame
        frame.add(new JLabel("Month (1-12):"));
        frame.add(monthField);
        frame.add(new JLabel("Day (1-31):"));
        frame.add(dayField);
        frame.add(new JLabel("Year:"));
        frame.add(yearField);
        frame.add(calculateButton);
        frame.add(resultLabel);

        // Action listener for the calculate button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get input values
                String monthInput = monthField.getText();
                String dayInput = dayField.getText();
                String yearInput = yearField.getText();

                // Validate input
                if (!isValidInput(monthInput, dayInput, yearInput)) {
                    resultLabel.setText("Invalid input. Please enter valid month, day, and year.");
                    return;
                }

                int m = Integer.parseInt(monthInput);
                int d = Integer.parseInt(dayInput);
                int y = Integer.parseInt(yearInput);

                // Calculate the day of the week
                int d0 = calculateD0(m, d, y);

                // Get the corresponding day of the week
                DaysOfTheWeek day = findDay(d0);
                String dayOfWeek = (day != null) ? day.name() : "Invalid date";

                // Display the result
                resultLabel.setText("Result: " + dayOfWeek);
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    private static boolean isValidInput(String month, String day, String year) {
        try {
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);
            int y = Integer.parseInt(year);
            return m >= 1 && m <= 12 && d >= 1 && d <= 31 && y > 0; // Basic validation
        } catch (NumberFormatException e) {
            return false; // Invalid number format
        }
    }

    enum DaysOfTheWeek {
        MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);
        private final int value;

        DaysOfTheWeek(int value) {
            this.value = value;
        }
    }

}

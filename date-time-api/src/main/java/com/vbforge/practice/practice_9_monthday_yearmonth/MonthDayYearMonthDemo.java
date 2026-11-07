package com.vbforge.practice.practice_9_monthday_yearmonth;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Demonstrates {@link MonthDay} and {@link YearMonth} — partial dates.
 */
public class MonthDayYearMonthDemo {

    public static void main(String[] args) {

        //monthday
        MonthDay md1 = MonthDay.now();
        MonthDay md2 = MonthDay.of(1, 16);
        LocalDate newLocalDate = md2.atYear(2025);

        System.out.println("monthday");
        //to avoid dashes:
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM-dd");
        System.out.println(md1.format(formatter1)); // Will output: 01-16 without additional dashes.
        System.out.println(md1); //--01-16
        System.out.println(md1.getDayOfMonth());
        System.out.println(md1.getMonth());
        System.out.println(md1.getMonthValue());
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MMdd");
        System.out.println(md2.format(formatter2));// Will output: 0116 without additional dashes.
        System.out.println(newLocalDate + "\n");

        //yearmonth
        YearMonth ym1 = YearMonth.now();
        LocalDate yearMonthLocalDate = ym1.atDay(16);

        System.out.println("yearmonth");
        System.out.println(ym1);
        System.out.println(yearMonthLocalDate + "\n");

        System.out.println("==========");
    }
}

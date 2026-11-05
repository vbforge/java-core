package com.vbforge.practice.practice_9_anonymous_class_as_callback;

/** A field initialised with an anonymous implementation that has two methods; one calls the other. */
public class AnonymousFieldCallbackDemo {

    public static void main(String[] args) {
        BookServiceExtra bookService = new BookServiceExtra();
        int price = bookService.booking.calculatePrice(5, 50);
        String details = bookService.booking.details(5, 50);

        System.out.println("Calculated Price: $" + price);
        System.out.println(details);

        // fewer days: no discount
        price = bookService.booking.calculatePrice(2, 50);
        details = bookService.booking.details(2, 50);

        System.out.println("Calculated Price for 2 days: $" + price);
        System.out.println(details);
    }
}

class BookServiceExtra {
    Booking booking = new Booking() {
        @Override
        public int calculatePrice(int days, double price) {
            int totalPrice = days * (int) price;
            if (days > 3) {
                totalPrice -= (int) (totalPrice * 0.1);   // 10% discount for more than 3 days
            }
            return totalPrice;
        }

        @Override
        public String details(int days, double price) {
            int totalPrice = calculatePrice(days, price);
            return "Total price for " + days + " days at $" + price + " per day is: $" + totalPrice;
        }
    };

    interface Booking {
        int calculatePrice(int days, double price);

        String details(int days, double price);
    }
}

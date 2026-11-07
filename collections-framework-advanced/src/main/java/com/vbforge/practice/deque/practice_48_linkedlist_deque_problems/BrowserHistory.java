package com.vbforge.practice.deque.practice_48_linkedlist_deque_problems;

import java.util.LinkedList;

public class BrowserHistory {

    private final LinkedList<String> history;
    private int currentIndex;

    public BrowserHistory(String homePage) {
        this.history = new LinkedList<>();
        this.history.add(homePage);
        this.currentIndex = 0;
    }

    //visit
    public void visit(String url){
        //remove all forward history
        while(history.size() > currentIndex + 1){
            history.removeLast();
        }
        history.add(url);
        currentIndex++;
    }

    //back
    public String back(int steps){
        currentIndex = Math.max(0, currentIndex - steps);
        return history.get(currentIndex);
    }

    //forward
    public String forward(int steps){
        currentIndex = Math.min(history.size() - 1, currentIndex + steps);
        return history.get(currentIndex);
    }

    public static void main(String[] args) {

        BrowserHistory browser = new BrowserHistory("leetcode.com");
        browser.visit("google.com");
        browser.visit("facebook.com");
        browser.visit("youtube.com");

        System.out.println(browser.back(1));     // facebook.com
        System.out.println(browser.back(1));     // google.com
        System.out.println(browser.forward(1));  // facebook.com
        browser.visit("linkedin.com");
        System.out.println(browser.forward(2));  // linkedin.com
        System.out.println(browser.back(2));     // google.com
        System.out.println(browser.back(7));     // leetcode.com

    }


}

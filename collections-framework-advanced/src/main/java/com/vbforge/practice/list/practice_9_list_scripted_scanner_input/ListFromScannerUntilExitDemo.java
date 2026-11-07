package com.vbforge.practice.list.practice_9_list_scripted_scanner_input;

import java.util.ArrayList;
import java.util.Scanner;

public class ListFromScannerUntilExitDemo {
  public static void main(String[] args) {

      ArrayList<String> list = new ArrayList<String>();

      list.add("Bananas");

      // scripted input instead of System.in, so the demo is deterministic (note the repeated "Bananas")
      Scanner scan = new Scanner("Apples Bananas Cherries Apples EXIT ignored-after-exit");
      System.out.println("Input 'EXIT' when you have finished");
      String item = "";
      do{
        item = scan.next();
        if(!list.contains(item) && !item.equals("EXIT")){
          list.add(item);
        }
      }
      while(!item.equals("EXIT"));

      System.out.println(list);
  }
}

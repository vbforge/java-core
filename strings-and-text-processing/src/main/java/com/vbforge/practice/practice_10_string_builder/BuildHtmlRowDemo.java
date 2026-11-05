package com.vbforge.practice.practice_10_string_builder;

/** Assemble a markup row from several values with chained appends. */
public class BuildHtmlRowDemo {

    public static void main(String[] args) {
        System.out.println("buildHTMLDynamically: " + buildHtmlRow("John", 26, "NY"));
    }

    static String buildHtmlRow(String name, int age, String city) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>")
          .append("<td>").append(name).append("</td>")
          .append("<td>").append(age).append("</td>")
          .append("<td>").append(city).append("</td>")
          .append("</tr>");
        return sb.toString();
    }
}

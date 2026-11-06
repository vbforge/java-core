package com.vbforge.practice.practice_17_template_method;

public class TemplateMethodReportDemo {

    public static void main(String[] args) {
        ReportGenerator sales = new SalesReport();
        sales.generateReport("Sales");

        System.out.println();

        ReportGenerator audit = new AuditReport();
        audit.generateReport("Audit");
    }
}

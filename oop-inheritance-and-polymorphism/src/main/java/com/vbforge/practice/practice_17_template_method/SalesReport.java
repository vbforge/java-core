package com.vbforge.practice.practice_17_template_method;

import com.vbforge.practice.practice_17_template_method.ReportGenerator;

public class SalesReport extends ReportGenerator {
    @Override protected void fetchData()  { System.out.println("  Querying sales database..."); }
    @Override protected void formatData() { System.out.println("  Formatting as bar chart..."); }
}

package com.vbforge.practice.practice_17_template_method;

import com.vbforge.practice.practice_17_template_method.ReportGenerator;

public class AuditReport extends ReportGenerator {
    @Override protected void fetchData()    { System.out.println("  Fetching audit logs..."); }
    @Override protected void formatData()   { System.out.println("  Formatting as table..."); }
    @Override protected void exportReport() { System.out.println("  Exporting encrypted PDF..."); } // overridden step
}

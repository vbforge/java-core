package com.vbforge.practice.practice_17_template_method;

/**
 * Template Method: the final method fixes the order of the steps, subclasses fill in the variable ones.
 */
public abstract class ReportGenerator {

    public final void generateReport(String title) {
        System.out.println("  === " + title + " Report ===");
        fetchData();
        formatData();
        exportReport();
        System.out.println("  === Done ===");
    }

    protected abstract void fetchData();

    protected abstract void formatData();

    // Hook with a default implementation; subclasses may override it
    protected void exportReport() {
        System.out.println("  Exporting to file system...");
    }
}

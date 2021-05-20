package test;

import org.junit.*;
import java.io.*;
import org.junit.runner.*;
import org.junit.runner.notification.*;

public abstract class JUnitListenerBase extends RunListener {
    private PrintWriter pw;
    private String outputFile = System.getProperty("junit.output.file");

    private final String id;

    public JUnitListenerBase(String id) {
        this.id = id;
    }

    public void testRunStarted(Description description) throws Exception {
        File file = new File(outputFile);
        pw = new PrintWriter(new FileWriter(outputFile, /*append=*/true));
    }
    public void testRunFinished(Result result) throws Exception {
        pw.println("testRunFinished_" + id);
        pw.close();
    }
    public void testStarted(Description description) throws Exception {
        pw.println("testStarted_" + id + " " + description.getDisplayName());
        pw.flush();
    }
    public void testFinished(Description description) throws Exception {
        pw.println("testFinished_" + id + " " + description.getDisplayName());
        pw.flush();
    }
    public void testFailure(Failure failure) throws Exception {
        pw.println("testFailure_" + id + " " + failure.getDescription().getDisplayName());
        pw.flush();
    }
    public void testAssumptionFailure(Failure failure) {
        pw.print("ASSUMPTION FAILURE");
        pw.flush();
    }
    public void testIgnored(Description description) throws Exception {
        pw.println("testIgnored_" + id + " " + description.getDisplayName());
        pw.flush();
    }
}
package test;

import org.junit.*;
import java.io.*;
import org.junit.runner.*;
import org.junit.runner.notification.*;

public class JUnitListener extends RunListener {
    private PrintWriter pw;
    private String outputFile = System.getProperty("junit.output.file");

    public void testRunStarted(Description description) throws Exception {
        pw = new PrintWriter(new FileWriter(outputFile));
        pw.println("testRunStarted");
    }
    public void testRunFinished(Result result) throws Exception {
        pw.println("testRunFinished");
        pw.close();
    }
    public void testStarted(Description description) throws Exception {
        pw.println("testStarted " + description.getDisplayName());
    }
    public void testFinished(Description description) throws Exception {
        pw.println("testFinished " + description.getDisplayName());
    }
    public void testFailure(Failure failure) throws Exception {
        pw.println("testFailure " + failure.getDescription().getDisplayName());
    }
    public void testAssumptionFailure(Failure failure) {
        pw.print("ASSUMPTION FAILURE");
    }
    public void testIgnored(Description description) throws Exception {
        pw.println("testIgnored " + description.getDisplayName());
    }
}
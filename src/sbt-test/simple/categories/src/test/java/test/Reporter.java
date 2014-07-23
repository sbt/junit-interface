package test;

import java.io.*;

public class Reporter {
    public static synchronized void report(String name) {
        try {
            Writer writer = new FileWriter("target/testsrun", true);
            writer.write(name + "\n");
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package test;

import org.junit.*;
import org.junit.Test;
import org.junit.experimental.categories.*;

public class TestOne {

    @Test
    @Category(Fast.class)
    public void fast() {
        Reporter.report("one-fast");
    }

    @Test
    @Category(Slow.class)
    public void slow() {
        Reporter.report("one-slow");
    }

    @Test
    public void none() {
        Reporter.report("one-none");
    }

}
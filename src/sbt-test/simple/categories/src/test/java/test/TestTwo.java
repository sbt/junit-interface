package test;

import org.junit.*;
import org.junit.Test;
import org.junit.experimental.categories.*;
import org.junit.experimental.categories.Category;

@Category(Fast.class)
public class TestTwo {

    @Test
    public void a() {
        Reporter.report("two-a");
    }

    @Test
    public void b() {
        Reporter.report("two-b");
    }

}
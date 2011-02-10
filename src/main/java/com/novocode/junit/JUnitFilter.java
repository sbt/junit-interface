// Mostly copied from http://stackoverflow.com/questions/1230706/running-a-subset-of-junit-test-methods/1236782#1236782
package com.novocode.junit;

import java.util.regex.*;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.Description;

public final class JUnitFilter extends Filter {
  private static final String DELIMITER = "\\,";

  private String[] testPatterns;

  public JUnitFilter(String testFilter) {
    if (testFilter != null) {
      testPatterns = testFilter.split(DELIMITER);
    }
  }

  public String describe() {
    return "Filters out all tests not explicitly named in a comma-delimited list in the system property 'tests'."; 
  }

  public boolean shouldRun(Description d) {
    String displayName = d.getDisplayName();
    
    // We get asked both if we should run the class/suite, as well as the individual tests
    // So let the suite always run, so we can evaluate the individual testcases
    if (displayName.indexOf('(') == -1) {
      return true;
    }
    String testName = displayName.substring(0, displayName.indexOf('('));
    if (testPatterns == null) return true;

    for (int i = 0; i < testPatterns.length; i++) {
      if (Pattern.matches(testPatterns[i], testName)) {
        return true;
      }
    }

    // If we have no test patterns, run everything
    // If we have any, and made it down here, return false
    return testPatterns == null || testPatterns.length == 0;
  }
}

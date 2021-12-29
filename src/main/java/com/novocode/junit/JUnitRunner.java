package com.novocode.junit;

import org.junit.runner.notification.RunListener;
import sbt.testing.Runner;
import sbt.testing.Task;
import sbt.testing.Selector;
import sbt.testing.TestSelector;
import sbt.testing.TaskDef;

import java.util.*;
import java.util.stream.Collectors;

final class JUnitRunner implements Runner {
  private final String[] args;
  private final String[] remoteArgs;
  private final RunSettings settings;

  private volatile boolean used = false;

  final ClassLoader testClassLoader;
  final List<RunListener> runListeners;
  final RunStatistics runStatistics;

  JUnitRunner(String[] args, String[] remoteArgs, ClassLoader testClassLoader) {
    this.args = args;
    this.remoteArgs = remoteArgs;
    this.testClassLoader = testClassLoader;

    boolean quiet = false, nocolor = false, decodeScalaNames = false,
        logAssert = true, logExceptionClass = true;
    RunSettings.Verbosity verbosity = RunSettings.Verbosity.TERSE;
    RunSettings.Summary summary = RunSettings.Summary.SBT;
    HashMap<String, String> sysprops = new HashMap<>();
    ArrayList<String> globPatterns = new ArrayList<>();
    Set<String> includeCategories = new HashSet<>();
    Set<String> excludeCategories = new HashSet<>();

    String testFilter = "";
    String ignoreRunners = "org.junit.runners.Suite";
    final List<String> runListeners = new ArrayList<>();
    for(String s : args) {
      if("-q".equals(s)) quiet = true;
      else if("-v".equals(s)) verbosity = RunSettings.Verbosity.STARTED;
      else if("+v".equals(s)) verbosity = RunSettings.Verbosity.TERSE;
      else if(s.startsWith("--verbosity=")) verbosity = RunSettings.Verbosity.values()[Integer.parseInt(s.substring(12))];
      else if(s.startsWith("--summary=")) summary = RunSettings.Summary.values()[Integer.parseInt(s.substring(10))];
      else if("-n".equals(s)) nocolor = true;
      else if("-s".equals(s)) decodeScalaNames = true;
      else if("-a".equals(s)) logAssert = true;
      else if("-c".equals(s)) logExceptionClass = false;
      else if(s.startsWith("--tests=")) testFilter = s.substring(8);
      else if(s.startsWith("--ignore-runners=")) ignoreRunners = s.substring(17);
      else if(s.startsWith("--run-listener=")) runListeners.add(s.substring(15));
      else if(s.startsWith("--include-categories=")) includeCategories.addAll(Arrays.asList(s.substring(21).split(",")));
      else if(s.startsWith("--exclude-categories=")) excludeCategories.addAll(Arrays.asList(s.substring(21).split(",")));
      else if(s.startsWith("-D") && s.contains("=")) {
        int sep = s.indexOf('=');
        sysprops.put(s.substring(2, sep), s.substring(sep+1));
      }
      else if(!s.startsWith("-") && !s.startsWith("+")) globPatterns.add(s);
    }
    for(String s : args) {
      if("+q".equals(s)) quiet = false;
      else if("+n".equals(s)) nocolor = false;
      else if("+s".equals(s)) decodeScalaNames = false;
      else if("+a".equals(s)) logAssert = false;
      else if("+c".equals(s)) logExceptionClass = true;
    }
    this.settings =
      new RunSettings(!nocolor, decodeScalaNames, quiet, verbosity, summary, logAssert, ignoreRunners, logExceptionClass,
        sysprops, globPatterns, includeCategories, excludeCategories,
        testFilter);
    this.runListeners = runListeners.stream()
            .map(this::createRunListener)
            .collect(Collectors.toList());
    this.runStatistics = new RunStatistics(settings);
  }

  @Override
  public Task[] tasks(TaskDef[] taskDefs) {
    used = true;
    Task[] tasks = Arrays
        .stream(taskDefs)
        .map(taskDef -> {
          RunSettings alteredSettings = alterRunSettings(this.settings, taskDef.selectors());
          return new JUnitTask(this, alteredSettings, taskDef);
        })
        .toArray(Task[]::new);
    return tasks;
  }

  /**
   * Alter default RunSettings depending on the passed selectors.
   * If selectors contains only elements of type TestSelector, then default settings are altered to include only test
   * names from these selectors. This allows to run particular test cases within given test class.
   * testFilter is treated as a regular expression, hence joining is done via '|'.
   */
  private RunSettings alterRunSettings(RunSettings defaultSettings, Selector[] selectors) {
    boolean onlyTestSelectors = Arrays.stream(selectors).allMatch(selector -> selector instanceof TestSelector);
    if (onlyTestSelectors) {
      String testFilter = Arrays
          .stream(selectors)
          .map(selector -> ((TestSelector) selector).testName())
          .collect(Collectors.joining("|"));
      // if already provided testFilter is not empty add to it | (regex or operator)
      String currentFilter = defaultSettings.testFilter.length() > 0 ? defaultSettings.testFilter + "|" : "";
      String newFilter = currentFilter + testFilter;
      return defaultSettings.withTestFilter(newFilter);
    }

    return defaultSettings;
  }

  private RunListener createRunListener(String runListenerClassName) {
    if(runListenerClassName != null) {
      try {
        return (RunListener) testClassLoader.loadClass(runListenerClassName).newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else return null;
  }

  @Override
  public String done() {
    // Can't simply return the summary due to https://github.com/sbt/sbt/issues/3510
    if(!used) return "";
    String stats = runStatistics.createSummary();
    if(stats.isEmpty()) return stats;
    System.out.println(stats);
    return " ";
  }

  @Override
  public String[] remoteArgs() {
    return remoteArgs;
  }

  @Override
  public String[] args() {
    return args;
  }
}

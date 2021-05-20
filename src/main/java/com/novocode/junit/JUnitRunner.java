package com.novocode.junit;

import org.junit.runner.notification.RunListener;
import sbt.testing.Runner;
import sbt.testing.Task;
import sbt.testing.TaskDef;

import java.util.*;


final class JUnitRunner implements Runner {
  private final String[] args;
  private final String[] remoteArgs;
  private final RunSettings settings;

  private volatile boolean used = false;

  final ClassLoader testClassLoader;
  final RunListener runListener;
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
    String runListener = null;
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
      else if(s.startsWith("--run-listener=")) runListener = s.substring(15);
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
    this.runListener = createRunListener(runListener);
    this.runStatistics = new RunStatistics(settings);
  }

  @Override
  public Task[] tasks(TaskDef[] taskDefs) {
    used = true;
    int length = taskDefs.length;
    Task[] tasks = new Task[length];
    for (int i = 0; i < length; i++) {
      TaskDef taskDef = taskDefs[i];
      tasks[i] = new JUnitTask(this, settings, taskDef);
    }
    return tasks;
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

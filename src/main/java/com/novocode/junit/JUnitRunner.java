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

  final ClassLoader testClassLoader;
  final RunListener runListener;

  JUnitRunner(String[] args, String[] remoteArgs, ClassLoader testClassLoader) {
    this.args = args;
    this.remoteArgs = remoteArgs;
    this.testClassLoader = testClassLoader;

    boolean quiet = false, verbose = true, nocolor = false, decodeScalaNames = false,
        logAssert = true, logExceptionClass = true;
    HashMap<String, String> sysprops = new HashMap<String, String>();
    ArrayList<String> globPatterns = new ArrayList<String>();
    Set<String> includeCategories = new HashSet<String>();
    Set<String> excludeCategories = new HashSet<String>();

    String testFilter = "";
    String ignoreRunners = "org.junit.runners.Suite";
    String runListener = null;
    for(String s : args) {
      if("-q".equals(s)) quiet = true;
      else if("-v".equals(s)) verbose = true;
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
      else if("+v".equals(s)) verbose = false;
      else if("+n".equals(s)) nocolor = false;
      else if("+s".equals(s)) decodeScalaNames = false;
      else if("+a".equals(s)) logAssert = false;
      else if("+c".equals(s)) logExceptionClass = true;
    }
    this.settings =
      new RunSettings(!nocolor, decodeScalaNames, quiet, verbose, logAssert, ignoreRunners, logExceptionClass,
        sysprops, globPatterns, includeCategories, excludeCategories,
        testFilter);
    this.runListener = createRunListener(runListener);
  }

  @Override
  public Task[] tasks(TaskDef[] taskDefs) {
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
    return "";
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

package com.novocode.junit;

import java.lang.annotation.Annotation;
import java.util.*;

import junit.framework.TestCase;

import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import sbt.testing.EventHandler;
import sbt.testing.Fingerprint;
import sbt.testing.Logger;
import sbt.testing.Runner;
import sbt.testing.Task;
import sbt.testing.TaskDef;


final class JUnitRunner implements Runner {

  private static final Fingerprint JUNIT_FP = new JUnitFingerprint();
  private static final Object NULL = new Object();
  private final ClassLoader testClassLoader;
  private final String[] args;
  private final String[] remoteArgs;

  JUnitRunner(String[] args, String[] remoteArgs, ClassLoader testClassLoader) {
    this.args = args;
    this.remoteArgs = remoteArgs;
    this.testClassLoader = testClassLoader;
  }

  @Override
  public Task[] tasks(TaskDef[] taskDefs) {
    int length = taskDefs.length;
    Task[] tasks = new Task[length];
    for (int i = 0; i < length; i++) {
      TaskDef taskDef = taskDefs[i];
      tasks[i] = createTask(taskDef);
    }
    return tasks;
  }

  private Task createTask(final TaskDef taskDef) {
    return new Task() {
      @Override
      public String[] tags() {
        return new String[0];  // no tags yet
      }

      @Override
      public TaskDef taskDef() {
        return taskDef;
      }

      @Override
      public Task[] execute(EventHandler eventHandler, Logger[] loggers) {
        Fingerprint fingerprint = taskDef.fingerprint();
        String testClassName = taskDef.fullyQualifiedName();

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
          else if(s.startsWith("-tests=")) {
            for(Logger l : loggers) {
              l.warn("junit-interface option \"-tests\" is deprecated. Use \"--tests\" instead.");
            }
            testFilter = s.substring(7);
          }
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
        RunSettings settings =
            new RunSettings(!nocolor, decodeScalaNames, quiet, verbose, logAssert, ignoreRunners, logExceptionClass);
        RichLogger logger = new RichLogger(loggers, settings, testClassName);
        EventDispatcher ed = new EventDispatcher(logger, eventHandler, settings, fingerprint);
        JUnitCore ju = new JUnitCore();
        ju.addListener(ed);
        if (runListener != null) {
          ju.addListener(createRunListener(runListener));
        }

        HashMap<String, Object> oldprops = new HashMap<String, Object>();
        try {
          synchronized(System.getProperties()) {
            for(Map.Entry<String, String> me : sysprops.entrySet()) {
              String old = System.getProperty(me.getKey());
              oldprops.put(me.getKey(), old == null ? NULL : old);
            }
            for(Map.Entry<String, String> me : sysprops.entrySet()) {
              System.setProperty(me.getKey(), me.getValue());
            }
          }
          try {
            Class<?> cl = testClassLoader.loadClass(testClassName);
            if(shouldRun(fingerprint, cl, settings)) {
              Request request = Request.classes(cl);
              if(globPatterns.size() > 0) {
                request = new SilentFilterRequest(request, new GlobFilter(settings, globPatterns));
              }
              if(testFilter.length() > 0) {
                request = new SilentFilterRequest(request, new TestFilter(testFilter, ed));
              }
              if(!includeCategories.isEmpty() || !excludeCategories.isEmpty()) {
                request = new SilentFilterRequest(request,
                        CategoryFilter.categoryFilter(true, loadClasses(testClassLoader, includeCategories), true,
                                loadClasses(testClassLoader, excludeCategories)));
              }
              ju.run(request);
            }
          } catch(Exception ex) {
            ed.testExecutionFailed(testClassName, ex);
          }
        } finally {
          synchronized(System.getProperties()) {
            for(Map.Entry<String, Object> me : oldprops.entrySet()) {
              if(me.getValue() == NULL) {
                System.clearProperty(me.getKey());
              } else {
                System.setProperty(me.getKey(), (String)me.getValue());
              }
            }
          }
        }
        return new Task[0]; // junit tests do not nest
      }
    };
  }

  private RunListener createRunListener(String runListenerClassName) {
    try {
      return (RunListener) testClassLoader.loadClass(runListenerClassName).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean shouldRun(Fingerprint fingerprint, Class<?> clazz, RunSettings settings) {
    if(JUNIT_FP.equals(fingerprint)) {
      // Ignore classes which are matched by the other fingerprints
      if(TestCase.class.isAssignableFrom(clazz)) {
        return false;
      }
      for(Annotation a : clazz.getDeclaredAnnotations()) {
        if(a.annotationType().equals(RunWith.class)) return false;
      }
      return true;
    } else {
      RunWith rw = clazz.getAnnotation(RunWith.class);
      if(rw != null) {
        return !settings.ignoreRunner(rw.value().getName());
      }
      else return true;
    }
  }

  private static Set<Class<?>> loadClasses(ClassLoader classLoader, Set<String> classNames) throws ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<Class<?>>();
    for(String className : classNames) {
      classes.add(classLoader.loadClass(className));
    }
    return classes;
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

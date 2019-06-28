package com.novocode.junit;

import junit.framework.TestCase;
import org.junit.experimental.categories.Categories;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import sbt.testing.*;

import java.lang.annotation.Annotation;
import java.util.*;

final class JUnitTask implements Task {
  private static final Fingerprint JUNIT_FP = new JUnitFingerprint();

  private final JUnitRunner runner;
  private final RunSettings settings;
  private final TaskDef taskDef;

  public JUnitTask(JUnitRunner runner, RunSettings settings, TaskDef taskDef) {
    this.runner = runner;
    this.settings = settings;
    this.taskDef = taskDef;
  }

  @Override
  public String[] tags() {
    return new String[0];  // no tags yet
  }

  @Override
  public TaskDef taskDef() { return taskDef; }

  @Override
  public Task[] execute(EventHandler eventHandler, Logger[] loggers) {
    Fingerprint fingerprint = taskDef.fingerprint();
    String testClassName = taskDef.fullyQualifiedName();
    Description taskDescription = Description.createSuiteDescription(testClassName);
    RichLogger logger = new RichLogger(loggers, settings, testClassName);
    EventDispatcher ed = new EventDispatcher(logger, eventHandler, settings, fingerprint, taskDescription, runner.runStatistics);
    JUnitCore ju = new JUnitCore();
    ju.addListener(ed);
    if (runner.runListener != null) ju.addListener(runner.runListener);

    Map<String, Object> oldprops = settings.overrideSystemProperties();
    try {
      try {
        Class<?> cl = runner.testClassLoader.loadClass(testClassName);
        if(shouldRun(fingerprint, cl, settings)) {
          Request request = Request.classes(cl);
          if(settings.globPatterns.size() > 0) {
            request = new SilentFilterRequest(request, new GlobFilter(settings, settings.globPatterns));
          }
          if(settings.testFilter.length() > 0) {
            request = new SilentFilterRequest(request, new TestFilter(settings.testFilter, ed));
          }
          if(!settings.includeCategories.isEmpty() || !settings.excludeCategories.isEmpty()) {
            request = new SilentFilterRequest(request,
                Categories.CategoryFilter.categoryFilter(true, loadClasses(runner.testClassLoader, settings.includeCategories), true,
                    loadClasses(runner.testClassLoader, settings.excludeCategories)));
          }
          ju.run(request);
        }
      } catch(Exception ex) {
        ed.testExecutionFailed(testClassName, ex);
      }
    } finally {
      settings.restoreSystemProperties(oldprops);
    }
    return new Task[0]; // junit tests do not nest
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
}

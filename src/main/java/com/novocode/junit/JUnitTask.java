package com.novocode.junit;

import junit.framework.TestCase;
import org.junit.experimental.categories.Categories;
import org.junit.runner.*;
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

    runner.runListeners.forEach(ju::addListener);

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
          // If the resulting request yields zero atomic test descriptions, treat as empty suite.
          // Occurs with certain custom @RunWith runners that deliberately don't expose
          // atomic tests (e.g., capability matrix collapses to nothing). We short-circuit
          // to a graceful empty run to avoid downstream AIOOBEs
          Description rootDesc;
          try {
              rootDesc = request.getRunner().getDescription();
          } catch (Exception e) {
              logger.warn("Unable to obtain JUnit description for " + testClassName + ": " + e);
              // Uniform lifecycle even on exception path
              ed.testRunStarted(taskDescription);
              ed.testExecutionFailed(testClassName, e); // report failure during the run
              Result result = new Result();
              ed.testRunFinished(result);
              return new Task[0];
          }
          if (isEffectivelyEmpty(rootDesc)) {
              logger.debug("Suite " + testClassName + " contains no atomic tests – treating as empty.");
              // Emit start/finish so stats remain consistent, but no test events.
              Description startDesc = (rootDesc != null ? rootDesc : taskDescription);
              ed.testRunStarted(startDesc);
              Result result = new Result();
              ed.testRunFinished(result);
          } else {
              ju.run(request);
          }
       }
      } catch(Exception ex) {
          // Uniform lifecycle even on exception path
          ed.testRunStarted(taskDescription);
          ed.testExecutionFailed(testClassName, ex);
          Result result = new Result();
          ed.testRunFinished(result);
      }
    } finally {
      settings.restoreSystemProperties(oldprops);
    }
    return new Task[0]; // junit tests do not nest
  }

  private static boolean isEffectivelyEmpty(Description root) {
      if (root == null) return true;
      if (root.isTest()) return false;          // Root is an atomic test
      List<Description> children = root.getChildren();
      if (children.isEmpty()) return true;      // Non-test node, no children → empty
      Deque<Description> q = new ArrayDeque<>();
      for (Description child : children) {
          if (child.isTest()) return false;
          q.addLast(child);
      }
      while (!q.isEmpty()) {
          Description d = q.removeFirst();
          for (Description child : d.getChildren()) {
              if (child.isTest()) return false;
              q.addLast(child);
          }
      }
      return true; // no test nodes found
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
    Set<Class<?>> classes = new HashSet<>();
    for(String className : classNames) {
      classes.add(classLoader.loadClass(className));
    }
    return classes;
  }
}

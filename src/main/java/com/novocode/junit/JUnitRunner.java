package com.novocode.junit;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Fingerprint;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner2;


final class JUnitRunner extends Runner2 {

  private final ClassLoader testClassLoader;
  private final Logger[] loggers;
  private static final Object NULL = new Object();

  JUnitRunner(ClassLoader testClassLoader, Logger[] loggers) {
    this.testClassLoader = testClassLoader;
    this.loggers = loggers;
  }

  @Override
  public void run(String testClassName, Fingerprint fingerprint, EventHandler eventHandler, String [] args) {
    boolean quiet = false, verbose = false, nocolor = false, decodeScalaNames = false,
            logAssert = false, logExceptionClass = false;
    HashMap<String, String> sysprops = new HashMap<String, String>();
    ArrayList<String> globPatterns = new ArrayList<String>();
    String testFilter = "";
    String ignoreRunners = "org.junit.runners.Suite";
    for(String s : args) {
      if("-q".equals(s)) quiet = true;
      else if("-v".equals(s)) verbose = true;
      else if("-n".equals(s)) nocolor = true;
      else if("-s".equals(s)) decodeScalaNames = true;
      else if("-a".equals(s)) logAssert = true;
      else if("-c".equals(s)) logExceptionClass = true;
      else if(s.startsWith("-tests=")) {
        for(Logger l : loggers)
          l.warn("junit-interface option \"-tests\" is deprecated. Use \"--tests\" instead.");
        testFilter = s.substring(7);
      }
      else if(s.startsWith("--tests=")) testFilter = s.substring(8); 
      else if(s.startsWith("--ignore-runners=")) ignoreRunners = s.substring(17); 
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
      else if("+c".equals(s)) logExceptionClass = false;
    }
    RunSettings settings =
        new RunSettings(!nocolor, decodeScalaNames, quiet, verbose, logAssert, ignoreRunners, logExceptionClass);
    RichLogger logger = new RichLogger(loggers, settings, testClassName);
    EventDispatcher ed = new EventDispatcher(logger, eventHandler, settings);
    JUnitCore ju = new JUnitCore();
    ju.addListener(ed);

    HashMap<String, Object> oldprops = new HashMap<String, Object>();
    try {
      synchronized(System.getProperties()) {
        for(Map.Entry<String, String> me : sysprops.entrySet()) {
          String old = System.getProperty(me.getKey());
          oldprops.put(me.getKey(), old == null ? NULL : old);
        }
        for(Map.Entry<String, String> me : sysprops.entrySet())
          System.setProperty(me.getKey(), me.getValue());
      }
      try {
        Class<?> cl = testClassLoader.loadClass(testClassName);
        if(shouldRun(fingerprint, cl, settings)) {
          Request request = Request.classes(cl);
          if(globPatterns.size() > 0) request = new SilentFilterRequest(request, new GlobFilter(settings, globPatterns));
          if(testFilter.length() > 0) request = new SilentFilterRequest(request, new TestFilter(testFilter, ed));
          try { ju.run(request); } finally { ed.uncapture(true); }
        }
      }
      catch(Exception ex) { ed.testExecutionFailed(testClassName, ex); }
    }
    finally {
      synchronized(System.getProperties()) {
        for(Map.Entry<String, Object> me : oldprops.entrySet()) {
          if(me.getValue() == NULL) System.clearProperty(me.getKey());
          else System.setProperty(me.getKey(), (String)me.getValue());
        }
      }
    }
  }

  private static final Fingerprint JUNIT_FP = new JUnitFingerprint();

  private boolean shouldRun(Fingerprint fingerprint, Class<?> clazz, RunSettings settings) {
    if(JUNIT_FP.equals(fingerprint)) {
      // Ignore classes which are matched by the other fingerprints
      if(TestCase.class.isAssignableFrom(clazz)) return false;
      for(Annotation a : clazz.getDeclaredAnnotations()) {
        if(a.annotationType().equals(RunWith.class)) return false;
      }
      return true;
    } else {
      RunWith rw = clazz.getAnnotation(RunWith.class);
      if(rw != null) return !settings.ignoreRunner(rw.value().getName());
      else return true;
    }
  }
}

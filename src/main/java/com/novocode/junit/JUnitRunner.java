package com.novocode.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Fingerprint;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner2;


final class JUnitRunner extends Runner2
{
  private final ClassLoader testClassLoader;
  private final Logger[] loggers;

  JUnitRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    this.testClassLoader = testClassLoader;
    this.loggers = loggers;
  }

  @Override
  public void run(String testClassName, Fingerprint fingerprint, EventHandler eventHandler, String [] args)
  {
    boolean quiet = false, verbose = false, nocolor = false;
    String testFilter = "";
    for(String s : args)
    {
      if("-q".equals(s)) quiet = true;
      else if("-v".equals(s)) verbose = true;
      else if("-n".equals(s)) nocolor = true;
      else if(s.startsWith("-tests=")) testFilter = s.substring(7);
    }
    for(String s : args)
    {
      if("+q".equals(s)) quiet = false;
      else if("+n".equals(s)) nocolor = false;
      else if("+v".equals(s)) verbose = false;
    }
    RichLogger logger = new RichLogger(loggers, !nocolor);
    EventDispatcher ed = new EventDispatcher(logger, eventHandler, quiet, verbose);
    JUnitCore ju = new JUnitCore();
    ju.addListener(ed);

    try
    {
      Class<?> cl = testClassLoader.loadClass(testClassName);
      Request request = Request.classes(cl);
      if(testFilter.length() > 0) request = request.filterWith(new JUnitFilter(testFilter, ed));
      try { ju.run(request); } finally { ed.uncapture(true); }
    }
    catch(Exception ex) { ed.post(new TestExecutionFailedEvent(testClassName, ex)); }
  }
}

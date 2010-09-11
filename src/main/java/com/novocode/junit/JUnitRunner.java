package com.novocode.junit;

import org.junit.runner.JUnitCore;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Fingerprint;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner2;


final class JUnitRunner extends Runner2
{
  private final ClassLoader testClassLoader;
  private final RichLogger logger;

  JUnitRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    this.testClassLoader = testClassLoader;
    this.logger = new RichLogger(loggers);
  }

  @Override
  public void run(String testClassName, Fingerprint fingerprint, EventHandler eventHandler, String [] args)
  {
    boolean quiet = false, verbose = false;
    for(String s : args)
    {
      if("-q".equals(s)) quiet = true;
      else if("-v".equals(s)) verbose = true;
    }
    EventDispatcher ed = new EventDispatcher(logger, eventHandler, quiet, verbose);
    JUnitCore ju = new JUnitCore();
    ju.addListener(ed);
    try
    {
      Class<?> cl = testClassLoader.loadClass(testClassName);
      try { ju.run(cl); } finally { ed.uncapture(true); }
    }
    catch(Exception ex) { ed.post(new TestExecutionFailedEvent(testClassName, ex)); }
  }
}

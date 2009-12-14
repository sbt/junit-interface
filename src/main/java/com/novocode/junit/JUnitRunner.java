package com.novocode.junit;

import org.junit.runner.JUnitCore;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner;
import org.scalatools.testing.TestFingerprint;


final class JUnitRunner implements Runner
{
  private final ClassLoader testClassLoader;
  private final RichLogger logger;

  JUnitRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    this.testClassLoader = testClassLoader;
    this.logger = new RichLogger(loggers);
  }

  @Override
  public void run(String testClassName, TestFingerprint fingerprint, EventHandler eventHandler, String [] args)
  {
    EventDispatcher ed = new EventDispatcher(logger, eventHandler);
    JUnitCore ju = new JUnitCore();
    ju.addListener(ed);
    try
    {
      Class<?> cl = testClassLoader.loadClass(testClassName);
      ju.run(cl);
    }
    catch(Exception ex) { ed.post(new TestExecutionFailedEvent(testClassName, ex)); }
  }
}

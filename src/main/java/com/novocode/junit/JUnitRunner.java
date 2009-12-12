package com.novocode.junit;

import org.junit.runner.JUnitCore;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner;
import org.scalatools.testing.TestFingerprint;


class JUnitRunner implements Runner
{
  private final ClassLoader cl;
  private final JUnitCore ju;
  private final RunListenerAdapter listener;

  JUnitRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    this.cl = testClassLoader;
    this.ju = new JUnitCore();
    Logger logger = loggers.length == 1 ? loggers[0] : new MultiplexLogger(loggers);
    this.listener = new RunListenerAdapter(logger);
    ju.addListener(listener);
  }

  @Override
  public void run(String testClassName, TestFingerprint fingerprint, EventHandler eventHandler, String [] args)
  {
    listener.setEventHandler(eventHandler);
    try
    {
      Class<?> test = cl.loadClass(testClassName);
      ju.run(test);
    }
    catch(Exception ex)
    {
      eventHandler.handle(new ErrorEvent(testClassName, "Test execution failed", ex));
    }
    finally { listener.setEventHandler(null); }
  }
}

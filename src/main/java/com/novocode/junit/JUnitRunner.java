package com.novocode.junit;

import java.lang.annotation.Annotation;

import org.junit.runner.JUnitCore;
import org.junit.runners.Suite.SuiteClasses;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Fingerprint;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner2;


final class JUnitRunner extends Runner2
{
  private static final String SUITE_ANNO = SuiteClasses.class.getName();
  private final ClassLoader testClassLoader;
  private final RichLogger logger;
  private final boolean ignoreSuites;

  JUnitRunner(ClassLoader testClassLoader, Logger[] loggers, boolean ignoreSuites)
  {
    this.testClassLoader = testClassLoader;
    this.logger = new RichLogger(loggers);
    this.ignoreSuites = ignoreSuites;
  }

  @Override
  public void run(String testClassName, Fingerprint fingerprint, EventHandler eventHandler, String [] args)
  {
    EventDispatcher ed = new EventDispatcher(logger, eventHandler);
    JUnitCore ju = new JUnitCore();
    ju.addListener(ed);
    try
    {
      Class<?> cl = testClassLoader.loadClass(testClassName);
      if(ignoreSuites)
      {
        for(Annotation a : cl.getAnnotations())
        {
          if(a.annotationType().getName().equals(SUITE_ANNO)) return;
        }
      }
      ju.run(cl);
    }
    catch(Exception ex) { ed.post(new TestExecutionFailedEvent(testClassName, ex)); }
  }
}

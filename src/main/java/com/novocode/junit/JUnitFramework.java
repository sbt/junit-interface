package com.novocode.junit;

import org.scalatools.testing.Framework;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner;
import org.scalatools.testing.TestFingerprint;


public final class JUnitFramework implements Framework
{
  private static final TestFingerprint[] FINGERPRINTS = new TestFingerprint[] {
    new TestFingerprint()
    {
      @Override
      public String superClassName() { return TestMarker.class.getName(); }
    
      @Override
      public boolean isModule() { return false; }
    }
  };

  @Override
  public String name() { return "JUnit"; }

  @Override
  public Runner testRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    return new JUnitRunner(testClassLoader, loggers);
  }

  @Override
  public TestFingerprint[] tests() { return FINGERPRINTS; }
}

package com.novocode.junit;

import org.scalatools.testing.Framework;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner;
import org.scalatools.testing.TestFingerprint;


public final class JUnitFrameworkNoMarker implements Framework
{
  private static final TestFingerprint[] FINGERPRINTS = new TestFingerprint[] { new NoMarkerFingerprint() };

  @Override
  public String name() { return "JUnit-NoMarker"; }

  @Override
  public Runner testRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    return new JUnitRunner(testClassLoader, loggers, true);
  }

  @Override
  public TestFingerprint[] tests() { return FINGERPRINTS; }
}

package com.novocode.junit;

import org.scalatools.testing.Framework;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner;
import org.scalatools.testing.Fingerprint;


public final class JUnitFramework implements Framework
{
  private static final Fingerprint[] FINGERPRINTS = new Fingerprint[] { new JUnitFingerprint() };

  @Override
  public String name() { return "JUnit"; }

  @Override
  public Runner testRunner(ClassLoader testClassLoader, Logger[] loggers)
  {
    return new JUnitRunner(testClassLoader, loggers, false);
  }

  @Override
  public Fingerprint[] tests() { return FINGERPRINTS; }
}

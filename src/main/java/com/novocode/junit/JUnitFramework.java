package com.novocode.junit;

import org.scalatools.testing.Fingerprint;
import org.scalatools.testing.Framework;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Runner;


public final class JUnitFramework implements Framework
{
  private static final Fingerprint[] FINGERPRINTS = new Fingerprint[] {
    new JUnitFingerprint(),
    new JUnit3Fingerprint(),
    new RunWithFingerprint()
  };

  @Override
  public String name() { return "JUnit"; }

  @Override
  public Runner testRunner(ClassLoader testClassLoader, Logger[] loggers) {
    return new JUnitRunner(testClassLoader, loggers);
  }

  @Override
  public Fingerprint[] tests() { return FINGERPRINTS; }
}

package com.novocode.junit;

import sbt.testing.Fingerprint;
import sbt.testing.Framework;


public final class JUnitFramework implements Framework
{
  private static final Fingerprint[] FINGERPRINTS = new Fingerprint[] {
    new RunWithFingerprint(),
    new JUnitFingerprint(),
    new JUnit3Fingerprint()
  };

  @Override
  public String name() { return "JUnit"; }

  @Override
  public sbt.testing.Fingerprint[] fingerprints() {
    return FINGERPRINTS;
  }

  @Override
  public sbt.testing.Runner runner(String[] args, String[] remoteArgs, ClassLoader testClassLoader) {
    return new JUnitRunner(args, remoteArgs, testClassLoader);
  }
}

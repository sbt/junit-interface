package com.novocode.junit;

import org.scalatools.testing.Fingerprint;

abstract public class AbstractFingerprint implements Fingerprint {
  public boolean shouldRun(Class<?> clazz) { return true; }
}

package com.novocode.junit;

import org.scalatools.testing.SubclassFingerprint;


public class JUnit3Fingerprint extends AbstractFingerprint implements SubclassFingerprint
{
  @Override
  public String superClassName() { return "junit.framework.TestCase"; }

  @Override
  public boolean isModule() { return false; }
}

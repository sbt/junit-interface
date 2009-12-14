package com.novocode.junit;

import org.scalatools.testing.TestFingerprint;


public class JUnitFingerprint implements TestFingerprint
{
  @Override
  public String superClassName() { return TestMarker.class.getName(); }

  @Override
  public boolean isModule() { return false; }
}

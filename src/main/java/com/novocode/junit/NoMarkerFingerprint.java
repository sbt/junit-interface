package com.novocode.junit;

import org.scalatools.testing.TestFingerprint;


public class NoMarkerFingerprint implements TestFingerprint
{
  @Override
  public String superClassName() { return Object.class.getName(); }

  @Override
  public boolean isModule() { return false; }
}

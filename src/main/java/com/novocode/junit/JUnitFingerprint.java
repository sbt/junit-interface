package com.novocode.junit;

import org.scalatools.testing.AnnotatedFingerprint;


public class JUnitFingerprint implements AnnotatedFingerprint
{
  @Override
  public String annotationName() { return "org.junit.Test"; }

  @Override
  public boolean isModule() { return false; }
}

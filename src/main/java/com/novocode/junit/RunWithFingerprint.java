package com.novocode.junit;

import org.scalatools.testing.AnnotatedFingerprint;


public class RunWithFingerprint extends AbstractFingerprint implements AnnotatedFingerprint
{
  @Override
  public String annotationName() { return "org.junit.runner.RunWith"; }

  @Override
  public boolean isModule() { return false; }
}

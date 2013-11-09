package com.novocode.junit;

import sbt.testing.AnnotatedFingerprint;


public class RunWithFingerprint implements AnnotatedFingerprint
{
  @Override
  public String annotationName() { return "org.junit.runner.RunWith"; }

  @Override
  public boolean isModule() { return false; }
}

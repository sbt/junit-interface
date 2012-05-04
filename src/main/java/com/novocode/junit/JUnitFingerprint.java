package com.novocode.junit;

import org.scalatools.testing.AnnotatedFingerprint;


public class JUnitFingerprint implements AnnotatedFingerprint {
  @Override
  public String annotationName() { return "org.junit.Test"; }

  @Override
  public boolean isModule() { return false; }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof AnnotatedFingerprint)) return false;
    AnnotatedFingerprint f = (AnnotatedFingerprint)obj;
    return annotationName().equals(f.annotationName()) && isModule() == f.isModule();
  }
}

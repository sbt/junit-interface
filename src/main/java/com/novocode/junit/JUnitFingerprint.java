package com.novocode.junit;

import java.lang.annotation.Annotation;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.scalatools.testing.AnnotatedFingerprint;


public class JUnitFingerprint extends AbstractFingerprint implements AnnotatedFingerprint
{
  @Override
  public String annotationName() { return "org.junit.Test"; }

  @Override
  public boolean isModule() { return false; }

  @Override
  public boolean shouldRun(Class<?> clazz) {
    // Ignore classes which are matched by the other fingerprints
    if(TestCase.class.isAssignableFrom(clazz)) return false;
    for(Annotation a : clazz.getDeclaredAnnotations()) {
      if(a.annotationType().equals(RunWith.class)) return false;
    }
    return true;
  }
}

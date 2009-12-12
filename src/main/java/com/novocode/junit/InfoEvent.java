package com.novocode.junit;

import org.junit.runner.Description;
import org.scalatools.testing.Result;


final class InfoEvent extends AbstractEvent
{
  InfoEvent(Description descr, String msg, Result result) { super(buildName(descr), msg, result); }

  @Override
  public Throwable error() { return null; }
}

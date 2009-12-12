package com.novocode.junit;

import org.scalatools.testing.Result;


final class ErrorEvent extends AbstractEvent
{
  private final Throwable err;

  ErrorEvent(String testName, String msg, Throwable err)
  {
    super(testName, msg, Result.Error);
    this.err = err;
  }

  @Override
  public Throwable error() { return err; }
}

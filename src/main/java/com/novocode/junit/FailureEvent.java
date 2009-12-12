package com.novocode.junit;

import org.junit.runner.notification.Failure;
import org.scalatools.testing.Result;


final class FailureEvent extends AbstractEvent
{
  private final Throwable err;

  FailureEvent(Failure failure)
  {
    super(buildName(failure.getDescription()), failure.getMessage(), Result.Failure);
    this.err = failure.getException();
  }

  @Override
  public Throwable error() { return err; }
}

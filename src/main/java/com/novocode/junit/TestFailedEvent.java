package com.novocode.junit;

import org.junit.runner.notification.Failure;
import org.scalatools.testing.Result;


final class TestFailedEvent extends AbstractEvent
{
  private final Failure failure;

  TestFailedEvent(Failure failure)
  {
    super(buildErrorName(failure.getDescription()), failure.getMessage(), Result.Failure, failure.getException());
    this.failure = failure;
  }

  @Override
  public void logTo(RichLogger logger)
  {
    logger.error("Test "+ansiName+" failed: "+failure.getMessage(), error);
  }
}

package com.novocode.junit;

import org.junit.runner.notification.Failure;
import org.scalatools.testing.Result;


final class TestAssumptionFailedEvent extends AbstractEvent
{
  private final Failure failure;

  TestAssumptionFailedEvent(Failure failure)
  {
    super(buildName(failure.getDescription()), failure.getMessage(), Result.Skipped, failure.getException());
    this.failure = failure;
  }

  @Override
  public void logTo(RichLogger logger)
  {
    logger.warn("Test assumption in test "+AbstractEvent.buildName(failure.getDescription())+" failed: "+failure.getMessage());
  }
}

package com.novocode.junit;

import org.scalatools.testing.Result;


final class TestExecutionFailedEvent extends AbstractEvent
{
  TestExecutionFailedEvent(String testName, Throwable err)
  {
    super(testName, "Test execution failed", Result.Error, err);
  }

  @Override
  public void logTo(RichLogger logger)
  {
    logger.error("Execution of test "+testName+" failed", error);
  }
}

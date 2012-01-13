package com.novocode.junit;

import org.scalatools.testing.Result;


final class TestExecutionFailedEvent extends AbstractEvent
{
  TestExecutionFailedEvent(String testName, Throwable err)
  {
    super(Ansi.c(testName, Ansi.ERRMSG), "Test execution failed", Result.Error, err);
  }

  @Override
  public void logTo(RichLogger logger)
  {
    logger.error("Execution of test "+ansiName+" failed", error);
  }
}

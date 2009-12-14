package com.novocode.junit;

import org.junit.runner.Description;
import org.scalatools.testing.Result;


final class TestFinishedEvent extends AbstractEvent
{
  TestFinishedEvent(Description desc) { super(buildName(desc), null, Result.Success, null); }

  @Override
  public void logTo(RichLogger logger)
  {
    logger.debug("Test "+testName+" finished");
  }
}

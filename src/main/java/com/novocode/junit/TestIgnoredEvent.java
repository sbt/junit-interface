package com.novocode.junit;

import org.junit.runner.Description;
import org.scalatools.testing.Result;


final class TestIgnoredEvent extends AbstractEvent
{
  TestIgnoredEvent(Description desc) { super(buildName(desc), null, Result.Skipped, null); }

  @Override
  public void logTo(RichLogger logger)
  {
    logger.info("Test "+testName+" ignored");
  }
}

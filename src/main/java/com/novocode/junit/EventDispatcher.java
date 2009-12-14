package com.novocode.junit;

import java.util.HashSet;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.scalatools.testing.EventHandler;


final class EventDispatcher extends RunListener
{
  private final RichLogger logger;
  private final HashSet<String> reported = new HashSet<String>();
  private final EventHandler handler;

  EventDispatcher(RichLogger logger, EventHandler handler)
  {
    this.logger = logger;
    this.handler = handler;
  }

  @Override
  public void testAssumptionFailure(Failure failure) { postIfFirst(new TestAssumptionFailedEvent(failure)); }

  @Override
  public void testFailure(Failure failure) { postIfFirst(new TestFailedEvent(failure)); }

  @Override
  public void testFinished(Description desc) { postIfFirst(new TestFinishedEvent(desc)); }

  @Override
  public void testIgnored(Description desc) { postIfFirst(new TestIgnoredEvent(desc)); }

  @Override
  public void testStarted(Description description)
  {
    logger.debug("Test "+AbstractEvent.buildName(description)+" started");
  }

  @Override
  public void testRunFinished(Result result)
  {
    logger.debug("Test run finished: "+result.getFailureCount()+" failed, "+result.getIgnoreCount()+" ignored, "+
      result.getRunCount()+" total, "+(result.getRunTime()/1000.0)+"s");
  }

  @Override
  public void testRunStarted(Description description)
  {
    logger.debug("Test run "+AbstractEvent.buildName(description)+" started");
  }

  private void postIfFirst(AbstractEvent e)
  {
    e.logTo(logger);
    if(reported.add(e.testName())) handler.handle(e);
  }

  void post(AbstractEvent e)
  {
    e.logTo(logger);
    handler.handle(e);
  }
}

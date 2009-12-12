package com.novocode.junit;

import java.util.HashSet;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.scalatools.testing.EventHandler;
import org.scalatools.testing.Logger;
import org.scalatools.testing.Result;


final class RunListenerAdapter extends RunListener
{
  private final Logger logger;
  private final HashSet<String> reported = new HashSet<String>();
  private EventHandler handler;

  RunListenerAdapter(Logger logger) { this.logger = logger; }

  void setEventHandler(EventHandler handler) { this.handler = handler; }

  @Override
  public void testAssumptionFailure(Failure failure)
  {
    logger.error("Test assumption in test "+AbstractEvent.buildName(failure.getDescription())+" failed: "+failure.getMessage());
    post(new FailureEvent(failure));
  }

  @Override
  public void testFailure(Failure failure) throws Exception
  {
    logger.error("Test "+AbstractEvent.buildName(failure.getDescription())+" failed: "+failure.getMessage());
    post(new FailureEvent(failure));
  }

  @Override
  public void testFinished(Description description) throws Exception
  {
    logger.debug("Test "+AbstractEvent.buildName(description)+" finished");
    post(new InfoEvent(description, null, Result.Success));
  }

  @Override
  public void testIgnored(Description description) throws Exception
  {
    logger.debug("Test "+AbstractEvent.buildName(description)+" ignored");
    post(new InfoEvent(description, null, Result.Skipped));
  }

  @Override
  public void testStarted(Description description) throws Exception
  {
    logger.debug("Test "+AbstractEvent.buildName(description)+" started");
  }

  private void post(AbstractEvent e)
  {
    if(reported.add(e.testName()) && handler != null) handler.handle(e);
  }

  @Override
  public void testRunFinished(org.junit.runner.Result result) throws Exception
  {
    logger.debug("Test run finished: "+result.getFailureCount()+" failed, "+result.getIgnoreCount()+" ignored, "+
      result.getRunCount()+" total, "+(result.getRunTime()/1000.0)+"s");
  }

  @Override
  public void testRunStarted(Description description) throws Exception
  {
    logger.debug("Test run "+AbstractEvent.buildName(description)+" started");
  }
}

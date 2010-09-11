package com.novocode.junit;

import java.io.IOException;
import java.util.HashSet;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.scalatools.testing.EventHandler;

import com.novocode.junit.OutputRedirector.Capture;


final class EventDispatcher extends RunListener
{
  private final RichLogger logger;
  private final HashSet<String> reported = new HashSet<String>();
  private final EventHandler handler;
  private final boolean quiet, verbose;
  private Capture capture;

  EventDispatcher(RichLogger logger, EventHandler handler, boolean quiet, boolean verbose)
  {
    this.logger = logger;
    this.handler = handler;
    this.quiet = quiet;
    this.verbose = verbose;
  }

  @Override
  public void testAssumptionFailure(Failure failure)
  {
    uncapture(true);
    postIfFirst(new TestAssumptionFailedEvent(failure));
  }

  @Override
  public void testFailure(Failure failure)
  {
    uncapture(true);
    postIfFirst(new TestFailedEvent(failure));
  }

  @Override
  public void testFinished(Description desc)
  {
    uncapture(false);
    postIfFirst(new TestFinishedEvent(desc));
  }

  @Override
  public void testIgnored(Description desc) { postIfFirst(new TestIgnoredEvent(desc)); }

  @Override
  public void testStarted(Description description)
  {
    debugOrInfo("Test "+AbstractEvent.buildName(description)+" started");
    capture();
  }

  @Override
  public void testRunFinished(Result result)
  {
    debugOrInfo("Test run finished: "+result.getFailureCount()+" failed, "+result.getIgnoreCount()+" ignored, "+
      result.getRunCount()+" total, "+(result.getRunTime()/1000.0)+"s");
  }

  @Override
  public void testRunStarted(Description description)
  {
    debugOrInfo("Test run started");
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

  private void capture()
  {
    if(quiet && capture == null)
      capture = OutputRedirector.capture();
  }

  void uncapture(boolean replay)
  {
    if(quiet && capture != null)
    {
      capture.stop();
      if(replay)
      {
        try { capture.replay(); }
        catch(IOException ex) { logger.error("Error replaying captured stdio", ex); }
      }
      capture = null;
    }
  }

  private void debugOrInfo(String msg)
  {
    if(verbose) logger.info(msg);
    else logger.debug(msg);
  }
}

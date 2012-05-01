package com.novocode.junit;

import java.io.IOException;
import java.util.HashSet;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.scalatools.testing.EventHandler;

import static com.novocode.junit.Ansi.*;


final class EventDispatcher extends RunListener
{
  private final RichLogger logger;
  private final HashSet<String> reported = new HashSet<String>();
  private final EventHandler handler;
  private final RunSettings settings;
  private OutputCapture capture;

  EventDispatcher(RichLogger logger, EventHandler handler, RunSettings settings)
  {
    this.logger = logger;
    this.handler = handler;
    this.settings = settings;
  }

  @Override
  public void testAssumptionFailure(final Failure failure)
  {
    uncapture(true);
    postIfFirst(new AbstractEvent(settings.buildErrorName(failure.getDescription()), failure.getMessage(), org.scalatools.testing.Result.Skipped, failure.getException()) {
      void logTo(RichLogger logger) {
        logger.warn("Test assumption in test "+ansiName+" failed: "+failure.getMessage());
      }
    });
  }

  @Override
  public void testFailure(final Failure failure)
  {
    uncapture(true);
    postIfFirst(new AbstractEvent(settings.buildErrorName(failure.getDescription()), failure.getMessage(), org.scalatools.testing.Result.Failure, failure.getException()) {
      void logTo(RichLogger logger) {
        logger.error("Test "+ansiName+" failed: "+failure.getMessage(), error);
      }
    });
  }

  @Override
  public void testFinished(Description desc)
  {
    uncapture(false);
    postIfFirst(new AbstractEvent(settings.buildInfoName(desc), null, org.scalatools.testing.Result.Success, null) {
      void logTo(RichLogger logger) {
        logger.debug("Test "+ansiName+" finished");
      }
    });
  }

  @Override
  public void testIgnored(Description desc)
  {
    postIfFirst(new AbstractEvent(settings.buildInfoName(desc), null, org.scalatools.testing.Result.Skipped, null) {
      void logTo(RichLogger logger) {
        logger.info("Test "+ansiName+" ignored");
      }
    });
  }

  @Override
  public void testStarted(Description description)
  {
    debugOrInfo("Test "+settings.buildInfoName(description)+" started");
    capture();
  }

  @Override
  public void testRunFinished(Result result)
  {
    debugOrInfo(c("Test run finished: ", INFO)+
      c(result.getFailureCount()+" failed", result.getFailureCount() > 0 ? ERRCOUNT : INFO)+
      c(", ", INFO)+
      c(result.getIgnoreCount()+" ignored", result.getIgnoreCount() > 0 ? IGNCOUNT : INFO)+
      c(", "+result.getRunCount()+" total, "+(result.getRunTime()/1000.0)+"s", INFO));
  }

  @Override
  public void testRunStarted(Description description)
  {
    debugOrInfo(c("Test run started", INFO));
  }

  void testExecutionFailed(String testName, Throwable err)
  {
    post(new AbstractEvent(Ansi.c(testName, Ansi.ERRMSG), "Test execution failed", org.scalatools.testing.Result.Error, err) {
      void logTo(RichLogger logger) {
        logger.error("Execution of test "+ansiName+" failed", error);
      }
    });
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
    if(settings.quiet && capture == null)
      capture = OutputCapture.start();
  }

  void uncapture(boolean replay)
  {
    if(settings.quiet && capture != null)
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
    if(settings.verbose) logger.info(msg);
    else logger.debug(msg);
  }
}

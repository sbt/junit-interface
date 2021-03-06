package com.novocode.junit;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import sbt.testing.EventHandler;
import sbt.testing.Fingerprint;
import sbt.testing.Status;

import static com.novocode.junit.Ansi.*;


final class EventDispatcher extends RunListener
{
  private final RichLogger logger;
  private final Set<String> reported = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private final ConcurrentHashMap<String, Long> startTimes = new ConcurrentHashMap<>();
  private final EventHandler handler;
  private final RunSettings settings;
  private final Fingerprint fingerprint;
  private final String taskInfo;
  private final RunStatistics runStatistics;
  private OutputCapture capture;

  EventDispatcher(RichLogger logger, EventHandler handler, RunSettings settings, Fingerprint fingerprint,
                  Description taskDescription, RunStatistics runStatistics)
  {
    this.logger = logger;
    this.handler = handler;
    this.settings = settings;
    this.fingerprint = fingerprint;
    this.taskInfo = settings.buildInfoName(taskDescription);
    this.runStatistics = runStatistics;
  }

  private abstract class Event extends AbstractEvent {
    Event(String name, String message, Status status, Long duration, Throwable error) {
      super(name, message, status, fingerprint, duration, error);
    }
    String durationSuffix() { return ", took " + durationToString(); }
  }

  private abstract class ErrorEvent extends Event {
    ErrorEvent(Failure failure, Status status) {
      super(settings.buildErrorName(failure.getDescription()),
            settings.buildErrorMessage(failure.getException()),
            status,
            elapsedTime(failure.getDescription()),
            failure.getException());
    }
  }

  private abstract class InfoEvent extends Event {
    InfoEvent(Description desc, Status status) {
      super(settings.buildInfoName(desc), null, status, elapsedTime(desc), null);
    }
  }

  @Override
  public void testAssumptionFailure(final Failure failure)
  {
    uncapture(true);
    postIfFirst(new ErrorEvent(failure, Status.Skipped) {
      void logTo(RichLogger logger) {
        logger.warn("Test assumption in test "+ansiName+" failed: "+ansiMsg + durationSuffix());
      }
    });
  }

  @Override
  public void testFailure(final Failure failure)
  {
    uncapture(true);
    postIfFirst(new ErrorEvent(failure, Status.Failure) {
      void logTo(RichLogger logger) {
        logger.error("Test "+ansiName+" failed: "+ansiMsg + durationSuffix(), error);
      }
    });
  }

  @Override
  public void testFinished(Description desc)
  {
    uncapture(false);
    postIfFirst(new InfoEvent(desc, Status.Success) {
      void logTo(RichLogger logger) {
        debugOrInfo("Test "+ansiName+" finished" + durationSuffix(), RunSettings.Verbosity.TEST_FINISHED);
      }
    });
    logger.popCurrentTestClassName();
  }

  @Override
  public void testIgnored(Description desc)
  {
    postIfFirst(new InfoEvent(desc, Status.Ignored) {
      void logTo(RichLogger logger) {
        logger.info("Test "+ansiName+" ignored");
      }
    });
  }

  @Override
  public void testStarted(Description description)
  {
    recordStartTime(description);
    logger.pushCurrentTestClassName(description.getClassName());
    debugOrInfo("Test " + settings.buildInfoName(description) + " started", RunSettings.Verbosity.STARTED);
    capture();
  }

  private void recordStartTime(Description description) {
    startTimes.putIfAbsent(settings.buildPlainName(description), System.currentTimeMillis());
  }

  private Long elapsedTime(Description description) {
    Long startTime = startTimes.get(settings.buildPlainName(description));
    if( startTime == null ) {
      return 0L;
    } else {
      return System.currentTimeMillis() - startTime;
    }
  }

  @Override
  public void testRunFinished(Result result)
  {
    debugOrInfo(c("Test run ", INFO)+taskInfo+c(" finished: ", INFO)+
      c(result.getFailureCount()+" failed", result.getFailureCount() > 0 ? ERRCOUNT : INFO)+
      c(", ", INFO)+
      c(result.getIgnoreCount()+" ignored", result.getIgnoreCount() > 0 ? IGNCOUNT : INFO)+
      c(", "+result.getRunCount()+" total, "+ result.getRunTime() / 1000.0 +"s", INFO), RunSettings.Verbosity.RUN_FINISHED);
    runStatistics.addTime(result.getRunTime());
  }

  @Override
  public void testRunStarted(Description description)
  {
    debugOrInfo(c("Test run ", INFO)+taskInfo+c(" started", INFO), RunSettings.Verbosity.STARTED);
  }

  void testExecutionFailed(String testName, Throwable err)
  {
    post(new Event(Ansi.c(testName, Ansi.ERRMSG), settings.buildErrorMessage(err), Status.Error, 0L, err) {
      void logTo(RichLogger logger) {
        logger.error("Execution of test "+ansiName+" failed: "+ansiMsg, error);
      }
    });
  }

  private void postIfFirst(AbstractEvent e)
  {
    e.logTo(logger);

    String fqn = e.fullyQualifiedName();
    if (reported.add(fqn)) {
      runStatistics.captureStats(e);
      handler.handle(e);
    }

    // NOTE: Status.Success is used to indicate that test is finished with any result (Success or Failure)
    // When test has failed, two events are actually generated:
    // 1) with Status.Failure
    // 2) with Status.Success (actually meaning that test has finished)
    // For non-failed tests, single event is emitted: Status.Success OR Status.Skipped OR Status.Ignored
    boolean testProcessed = e.status == Status.Success || e.status == Status.Skipped || e.status == Status.Ignored;
    if (testProcessed) {
      // JUnit can run tests with the same name multiple times: https://github.com/sbt/junit-interface/issues/96
      // Once test is finished, we mark it as unreported to allow running it again.
      //
      // There should be no issues with running tests in parallel (default behaviour of SBT)
      // For each JUnitTask a dedicated EventDispatcher will be created with it's own `reported` map
      reported.remove(fqn);
    }
  }

  private void post(AbstractEvent e)
  {
    e.logTo(logger);
    runStatistics.captureStats(e);
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

  private void debugOrInfo(String msg, RunSettings.Verbosity atVerbosity)
  {
    if(atVerbosity.ordinal() <= settings.verbosity.ordinal()) logger.info(msg);
    else logger.debug(msg);
  }
}

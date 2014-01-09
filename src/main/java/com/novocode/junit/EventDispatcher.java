package com.novocode.junit;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
  private final Set<String> reported = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
  private final ConcurrentHashMap<String, Long> startTimes = new ConcurrentHashMap<String, Long>();
  private final EventHandler handler;
  private final RunSettings settings;
  private final Fingerprint fingerprint;

  EventDispatcher(RichLogger logger, EventHandler handler, RunSettings settings, Fingerprint fingerprint)
  {
    this.logger = logger;
    this.handler = handler;
    this.settings = settings;
    this.fingerprint = fingerprint;
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
    postIfFirst(new ErrorEvent(failure, Status.Skipped) {
      void logTo(RichLogger logger) {
        logger.warn("Test assumption in test "+ansiName+" failed: "+ansiMsg + durationSuffix());
      }
    });
  }

  @Override
  public void testFailure(final Failure failure)
  {
    postIfFirst(new ErrorEvent(failure, Status.Failure) {
      void logTo(RichLogger logger) {
        logger.error("Test "+ansiName+" failed: "+ansiMsg + durationSuffix(), error);
      }
    });
  }

  @Override
  public void testFinished(Description desc)
  {
    postIfFirst(new InfoEvent(desc, Status.Success) {
      void logTo(RichLogger logger) {
        logger.debug("Test "+ansiName+" finished" + durationSuffix());
      }
    });
    logger.popCurrentTestClassName();
  }

  @Override
  public void testIgnored(Description desc)
  {
    postIfFirst(new InfoEvent(desc, Status.Skipped) {
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
    debugOrInfo("Test " + settings.buildInfoName(description) + " started");
  }

  private void recordStartTime(Description description) {
    startTimes.putIfAbsent(settings.buildPlainName(description), System.currentTimeMillis());
  }

  private Long elapsedTime(Description description) {
    Long startTime = startTimes.get(settings.buildPlainName(description));
    if( startTime == null ) {
      return 0l;
    } else {
      return System.currentTimeMillis() - startTime;
    }
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
    post(new Event(Ansi.c(testName, Ansi.ERRMSG), settings.buildErrorMessage(err), Status.Error, 0l, err) {
      void logTo(RichLogger logger) {
        logger.error("Execution of test "+ansiName+" failed: "+ansiMsg, error);
      }
    });
  }

  private void postIfFirst(AbstractEvent e)
  {
    e.logTo(logger);
    if(reported.add(e.fullyQualifiedName())) handler.handle(e);
  }

  void post(AbstractEvent e)
  {
    e.logTo(logger);
    handler.handle(e);
  }

  private void debugOrInfo(String msg)
  {
    if(settings.verbose) logger.info(msg);
    else logger.debug(msg);
  }
}

package com.novocode.junit;

import org.scalatools.testing.Event;
import org.scalatools.testing.Result;
import static com.novocode.junit.Ansi.*;

abstract class AbstractEvent implements Event
{
  protected final String ansiName;
  protected final String msg;
  protected final Result result;
  protected final Throwable error;

  AbstractEvent(String ansiName, String msg, Result result, Throwable error)
  {
    this.ansiName = ansiName;
    this.msg = msg;
    this.result = result;
    this.error = error;
  }

  @Override
  public final String testName() { return filterAnsi(ansiName); }

  @Override
  public final String description() { return msg; }

  @Override
  public final Result result() { return result; }

  @Override
  public final Throwable error() { return error; }

  abstract void logTo(RichLogger logger);
}

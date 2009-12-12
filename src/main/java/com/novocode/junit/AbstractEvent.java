package com.novocode.junit;

import org.junit.runner.Description;
import org.scalatools.testing.Event;
import org.scalatools.testing.Result;


abstract class AbstractEvent implements Event
{
  private final String testName;
  private final String msg;
  private final Result result;

  AbstractEvent(String testName, String msg, Result result)
  {
    this.testName = testName;
    this.msg = msg;
    this.result = result;
  }

  @Override
  public String testName() { return testName; }

  @Override
  public String description() { return msg; }

  @Override
  public Result result() { return result; }

  static String buildName(Description desc)
  {
    return desc.getClassName()+'#'+desc.getMethodName();
  }
}

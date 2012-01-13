package com.novocode.junit;

import org.junit.runner.Description;
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

  public void logTo(RichLogger logger) { }

  static String buildInfoName(Description desc)
  {
    return buildColoredName(desc, NNAME1, NNAME2, NNAME3);
  }

  static String buildErrorName(Description desc)
  {
    return buildColoredName(desc, ENAME1, ENAME2, ENAME3);
  }

  private static String buildColoredName(Description desc, String c1, String c2, String c3)
  {
    StringBuilder b = new StringBuilder();
    
    String cn = desc.getClassName();
    int pos1 = cn.indexOf('$');
    int pos2 = pos1 == -1 ? cn.lastIndexOf('.') : cn.lastIndexOf('.', pos1);
    if(pos2 == -1) b.append(c(cn, c1));
    else
    {
      b.append(cn.substring(0, pos2));
      b.append('.');
      b.append(c(cn.substring(pos2+1), c1));
    }

    b.append('.');

    String m = desc.getMethodName();
    int mpos1 = m.lastIndexOf('[');
    int mpos2 = m.lastIndexOf(']');
    if(mpos1 == -1 || mpos2 < mpos1) b.append(c(m, c2));
    else
    {
      b.append(c(m.substring(0, mpos1), c2));
      b.append('[');
      b.append(c(m.substring(mpos1+1, mpos2), c3));
      b.append(']');
    }

    return b.toString();
  }
}

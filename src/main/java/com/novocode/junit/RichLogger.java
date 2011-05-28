package com.novocode.junit;

import org.scalatools.testing.Logger;


final class RichLogger
{
  private final Logger[] loggers;

  RichLogger(Logger[] loggers) { this.loggers = loggers; }

  void debug(String s) { for(Logger l : loggers) l.debug(s); }

  void error(String s) { for(Logger l : loggers) l.error(s); }

  void error(String s, Throwable t)
  {
    error(s);
    if(t != null && !(t instanceof AssertionError)) logStackTrace(t);
  }

  void info(String s) { for(Logger l : loggers) l.info(s); }

  void warn(String s) { for(Logger l : loggers) l.warn(s); }

  private void logStackTrace(Throwable t)
  {
    StackTraceElement[] trace = t.getStackTrace();
    logStackTracePart(trace, trace.length-1, 0, t);
  }

  private void logStackTracePart(StackTraceElement[] trace, int m, int framesInCommon, Throwable t)
  {
    final int m0 = m;
    for(int i=0; i<=m; i++)
    {
      if(trace[i].toString().startsWith("org.junit."))
      {
        m = i-1;
        while(m > 0)
        {
          String s = trace[m].toString();
          if(!s.startsWith("java.lang.reflect.") && !s.startsWith("sun.reflect.")) break;
          m--;
        }
        break;
      }
    }
    for(int i=0; i<=m; i++) error("    at " + trace[i]);
    if(m0 != m)
    {
      // skip junit-related frames
      error("    ...");
    }
    else if(framesInCommon != 0)
    {
      // skip frames that were in the previous trace too
      error("    ... " + framesInCommon + " more");
    }
    logStackTraceAsCause(trace, t.getCause());
  }

  private void logStackTraceAsCause(StackTraceElement[] causedTrace, Throwable t)
  {
    if(t == null) return;
    StackTraceElement[] trace = t.getStackTrace();
    int m = trace.length - 1, n = causedTrace.length - 1;
    while(m >= 0 && n >= 0 && trace[m].equals(causedTrace[n]))
    {
      m--;
      n--;
    }
    error("Caused by: " + t);
    logStackTracePart(trace, m, trace.length-1-m, t);
  }
}

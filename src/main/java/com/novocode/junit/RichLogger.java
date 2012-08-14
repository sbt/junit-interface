package com.novocode.junit;

import java.util.Stack;
import org.scalatools.testing.Logger;
import static com.novocode.junit.Ansi.*;


final class RichLogger
{
  private final Logger[] loggers;
  private final RunSettings settings;
  /* The top element is the test class of the currently executing test */
  private final Stack<String> currentTestClassName = new Stack<String>();

  RichLogger(Logger[] loggers, RunSettings settings, String testClassName)
  {
    this.loggers = loggers;
    this.settings = settings;
    currentTestClassName.push(testClassName);
  }

  void pushCurrentTestClassName(String s) { currentTestClassName.push(s); }
  
  void popCurrentTestClassName()
  {
    if(currentTestClassName.size() > 1) currentTestClassName.pop();
  }

  void debug(String s)
  {
    for(Logger l : loggers)
      if(settings.color && l.ansiCodesSupported()) l.debug(s);
      else l.debug(filterAnsi(s));
  }

  void error(String s)
  {
    for(Logger l : loggers)
      if(settings.color && l.ansiCodesSupported()) l.error(s);
      else l.error(filterAnsi(s));
  }

  void error(String s, Throwable t)
  {
    error(s);
    if(t != null && (settings.logAssert || !(t instanceof AssertionError))) logStackTrace(t);
  }

  void info(String s)
  {
    for(Logger l : loggers)
      if(settings.color && l.ansiCodesSupported()) l.info(s);
      else l.info(filterAnsi(s));
  }

  void warn(String s)
  {
    for(Logger l : loggers)
      if(settings.color && l.ansiCodesSupported()) l.warn(s);
      else l.warn(filterAnsi(s));
  }

  private void logStackTrace(Throwable t)
  {
    StackTraceElement[] trace = t.getStackTrace();
    String testClassName = currentTestClassName.peek();
    String testFileName = settings.color ? findTestFileName(trace, testClassName) : null;
    logStackTracePart(trace, trace.length-1, 0, t, testClassName, testFileName);
  }

  private void logStackTracePart(StackTraceElement[] trace, int m, int framesInCommon, Throwable t, String testClassName, String testFileName)
  {
    final int m0 = m;
    int top = 0;
    for(int i=top; i<=m; i++)
    {
      if(trace[i].toString().startsWith("org.junit."))
      {
        if(i == top) top++;
        else
        {
          m = i-1;
          while(m > top)
          {
            String s = trace[m].toString();
            if(!s.startsWith("java.lang.reflect.") && !s.startsWith("sun.reflect.")) break;
            m--;
          }
          break;
        }
      }
    }
    for(int i=top; i<=m; i++) error("    at " + stackTraceElementToString(trace[i], testClassName, testFileName));
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
    logStackTraceAsCause(trace, t.getCause(), testClassName, testFileName);
  }

  private void logStackTraceAsCause(StackTraceElement[] causedTrace, Throwable t, String testClassName, String testFileName)
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
    logStackTracePart(trace, m, trace.length-1-m, t, testClassName, testFileName);
  }

  private String findTestFileName(StackTraceElement[] trace, String testClassName)
  {
    for(StackTraceElement e : trace)
    {
      String cln = e.getClassName();
      if(testClassName.equals(cln)) return e.getFileName();
    }
    return null;
  }

  private String stackTraceElementToString(StackTraceElement e, String testClassName, String testFileName)
  {
    boolean highlight = settings.color && (
        testClassName.equals(e.getClassName()) ||
        (testFileName != null && testFileName.equals(e.getFileName()))
      );
    StringBuilder b = new StringBuilder();
    b.append(settings.decodeName(e.getClassName() + '.' + e.getMethodName()));
    b.append('(');

    if(e.isNativeMethod()) b.append(c("Native Method", highlight ? TESTFILE2 : null));
    else if(e.getFileName() == null) b.append(c("Unknown Source", highlight ? TESTFILE2 : null));
    else
    {
      b.append(c(e.getFileName(), highlight ? TESTFILE1 : null));
      if(e.getLineNumber() >= 0)
        b.append(':').append(c(String.valueOf(e.getLineNumber()), highlight ? TESTFILE2 : null));
    }
    return b.append(')').toString();
  }

}

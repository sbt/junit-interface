package com.novocode.junit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


public class OutputRedirector extends OutputStream
{
  private static OutputRedirector redirectingOut;
  private static OutputRedirector redirectingErr;
  private static final PrintStream originalOut = System.out;
  private static final PrintStream originalErr = System.err;

  private final OutputStream parent;
  private final InheritableThreadLocal<OutputStream> threadOut = new InheritableThreadLocal<OutputStream>();

  private OutputRedirector(OutputStream parent) { this.parent = parent; }

  private OutputStream findDelegate()
  {
    OutputStream o = threadOut.get();
    return o == null ? parent : o;
  }

  private OutputStream setThreadOutputStream(OutputStream o)
  {
    OutputStream old = threadOut.get();
    threadOut.set(o);
    return old;
  }

  @Override
  public void write(int b) throws IOException { findDelegate().write(b); }

  @Override
  public void write(byte b[]) throws IOException { findDelegate().write(b); }

  @Override
  public void write(byte b[], int off, int len) throws IOException { findDelegate().write(b, off, len); }

  @Override
  public void flush() throws IOException { findDelegate().flush(); }

  @Override
  public void close() throws IOException { findDelegate().close(); }

  public static OutputStream setThreadOut(OutputStream out)
  {
    if(redirectingOut == null)
    {
      redirectingOut = new OutputRedirector(originalOut);
      System.setOut(new TransparentPrintStream(redirectingOut));
    }
    return redirectingOut.setThreadOutputStream(out);
  }

  public static OutputStream setThreadErr(OutputStream err)
  {
    if(redirectingErr == null)
    {
      redirectingErr = new OutputRedirector(originalErr);
      System.setErr(new TransparentPrintStream(redirectingErr));
    }
    return redirectingErr.setThreadOutputStream(err);
  }

  public static final class TransparentPrintStream extends PrintStream
  {
    public TransparentPrintStream(OutputStream out) { super(out, true); }

    public OutputStream getParent() { return out; }
  }

  public static final class Capture
  {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private OutputStream prevOut, prevErr;

    public void stop()
    {
      setThreadOut(prevOut);
      setThreadErr(prevErr);
    }

    public void replay() throws IOException
    {
      originalOut.write(out.toByteArray());
      originalOut.flush();
    }
  }

  public static Capture capture()
  {
    Capture c = new Capture();
    c.prevOut = setThreadOut(c.out);
    c.prevErr = setThreadErr(c.out);
    return c;
  }
}

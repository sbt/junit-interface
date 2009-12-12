package com.novocode.junit;

import org.scalatools.testing.Logger;


final class MultiplexLogger implements Logger
{
  private final Logger[] loggers;

  MultiplexLogger(Logger[] loggers) { this.loggers = loggers; }

  @Override
  public boolean ansiCodesSupported() { return false; }

  @Override
  public void debug(String s) { for(Logger l : loggers) l.debug(s); }

  @Override
  public void error(String s) { for(Logger l : loggers) l.error(s); }

  @Override
  public void info(String s) { for(Logger l : loggers) l.info(s); }

  @Override
  public void warn(String s) { for(Logger l : loggers) l.warn(s); }
}

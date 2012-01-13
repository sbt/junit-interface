package com.novocode.junit;

public class Ansi {
  // Standard ANSI sequences
  private static final String NORMAL = "\u001B[0m";
  private static final String HIGH_INTENSITY = "\u001B[1m";
  private static final String LOW_INTESITY = "\u001B[2m";
  private static final String BLACK = "\u001B[30m";
  private static final String RED = "\u001B[31m";
  private static final String GREEN = "\u001B[32m";
  private static final String YELLOW = "\u001B[33m";
  private static final String BLUE = "\u001B[34m";
  private static final String MAGENTA = "\u001B[35m";
  private static final String CYAN = "\u001B[36m";
  private static final String WHITE = "\u001B[37m";

  public static String c(String s, String colorSequence) { return colorSequence + s + NORMAL; }

  public static String filterAnsi(String s)
  {
    StringBuilder b = new StringBuilder(s.length());
    int len = s.length();
    for(int i=0; i<len; i++)
    {
      char c = s.charAt(i);
      if(c == '\u001B')
      {
        do { i++; } while(s.charAt(i) != 'm');
      }
      else b.append(c);
    }
    return b.toString();
  }

  private Ansi() {}

  public static final String INFO = BLUE;
  public static final String ERRCOUNT = RED;
  public static final String IGNCOUNT = YELLOW;
  public static final String ERRMSG = RED;
  public static final String NNAME1 = YELLOW;
  public static final String NNAME2 = CYAN;
  public static final String NNAME3 = YELLOW;
  public static final String ENAME1 = YELLOW;
  public static final String ENAME2 = RED;
  public static final String ENAME3 = YELLOW;
}

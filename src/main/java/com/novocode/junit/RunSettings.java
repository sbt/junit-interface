package com.novocode.junit;

import static com.novocode.junit.Ansi.ENAME1;
import static com.novocode.junit.Ansi.ENAME2;
import static com.novocode.junit.Ansi.ENAME3;
import static com.novocode.junit.Ansi.NNAME1;
import static com.novocode.junit.Ansi.NNAME2;
import static com.novocode.junit.Ansi.NNAME3;
import static com.novocode.junit.Ansi.c;

import java.lang.reflect.Method;
import java.util.HashSet;

import org.junit.runner.Description;

class RunSettings {
  final boolean color, quiet, verbose, logAssert, logExceptionClass;
  private final boolean decodeScalaNames;
  private final HashSet<String> ignoreRunners = new HashSet<String>(); 

  RunSettings(boolean color, boolean decodeScalaNames, boolean quiet,
      boolean verbose, boolean logAssert, String ignoreRunners,
      boolean logExceptionClass) {
    this.color = color;
    this.decodeScalaNames = decodeScalaNames;
    this.quiet = quiet;
    this.verbose = verbose;
    this.logAssert = logAssert;
    this.logExceptionClass = logExceptionClass;
    for(String s : ignoreRunners.split(","))
      this.ignoreRunners.add(s.trim());
  }

  String decodeName(String name) {
    return decodeScalaNames ? decodeScalaName(name) : name;
  }

  private static String decodeScalaName(String name) {
    try {
      Class<?> cl = Class.forName("scala.reflect.NameTransformer");
      Method m = cl.getMethod("decode", String.class);
      String decoded = (String)m.invoke(null, name);
      return decoded == null ? name : decoded;
    } catch(Throwable t) {
      //System.err.println("Error decoding Scala name:");
      //t.printStackTrace(System.err);
      return name;
    }
  }

  String buildInfoName(Description desc) {
    return buildColoredName(desc, NNAME1, NNAME2, NNAME3);
  }

  String buildErrorName(Description desc) {
    return buildColoredName(desc, ENAME1, ENAME2, ENAME3);
  }

  String buildPlainName(Description desc) {
    return buildColoredName(desc, null, null, null);
  }

  String buildColoredMessage(Throwable t, String c1) {
    if(t == null) return "null";
    if(!logExceptionClass || (!logAssert && (t instanceof AssertionError)))  return t.getMessage();
    StringBuilder b = new StringBuilder();

    String cn = decodeName(t.getClass().getName());
    int pos1 = cn.indexOf('$');
    int pos2 = pos1 == -1 ? cn.lastIndexOf('.') : cn.lastIndexOf('.', pos1);
    if(pos2 == -1) b.append(c(cn, c1));
    else {
      b.append(cn.substring(0, pos2));
      b.append('.');
      b.append(c(cn.substring(pos2+1), c1));
    }

    b.append(": ").append(t.getMessage());
    return b.toString();
  }

  String buildInfoMessage(Throwable t) {
    return buildColoredMessage(t, NNAME2);
  }

  String buildErrorMessage(Throwable t) {
    return buildColoredMessage(t, ENAME2);
  }

  private String buildColoredName(Description desc, String c1, String c2, String c3) {
    StringBuilder b = new StringBuilder();
    
    String cn = decodeName(desc.getClassName());
    int pos1 = cn.indexOf('$');
    int pos2 = pos1 == -1 ? cn.lastIndexOf('.') : cn.lastIndexOf('.', pos1);
    if(pos2 == -1) b.append(c(cn, c1));
    else {
      b.append(cn.substring(0, pos2));
      b.append('.');
      b.append(c(cn.substring(pos2+1), c1));
    }

    String m = desc.getMethodName();
    if(m != null) {
      b.append('.');
      int mpos1 = m.lastIndexOf('[');
      int mpos2 = m.lastIndexOf(']');
      if(mpos1 == -1 || mpos2 < mpos1) b.append(c(decodeName(m), c2));
      else {
        b.append(c(decodeName(m.substring(0, mpos1)), c2));
        b.append('[');
        b.append(c(m.substring(mpos1+1, mpos2), c3));
        b.append(']');
      }
    }

    return b.toString();
  }

  boolean ignoreRunner(String cln) { return ignoreRunners.contains(cln); }
}

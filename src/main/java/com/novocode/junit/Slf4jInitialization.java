package com.novocode.junit;

public class Slf4jInitialization {
  public static void initialize() {
    ClassLoader cl = Slf4jInitialization.class.getClassLoader();
    try {
      cl.loadClass("org.slf4j.LoggerFactory")
        .getMethod("getLogger", String.class)
        .invoke(null, "ROOT");
    } catch (Exception e) {
      // swallow - we might not have slf4j on the classpath
    }
  }
}

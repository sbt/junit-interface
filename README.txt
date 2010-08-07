An implementation of sbt's test interface <http://github.com/harrah/test-interface> for JUnit 4.
This enables you to run JUnit <http://www.junit.org/> tests from sbt.

Unlike Scala testing frameworks like ScalaTest (which can also run JUnit test cases), both JUnit
and this adapter are pure Java, so you can run JUnit tests with any Scala version supported by
sbt without having to build a binary-compatible test framework first.

See LICENSE.txt for licensing conditions (BSD-style).

To use with sbt 0.7, add the following dependency to your project:

  val junitInterface = "com.novocode" % "junit-interface" % "0.4" % "test->default"

JUnit itself is automatically pulled in as a transitive dependency.

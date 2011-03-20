An implementation of sbt's test interface <http://github.com/harrah/test-interface>
for JUnit 4. This allows you to run JUnit <http://www.junit.org/> tests from sbt.

Unlike Scala testing frameworks like ScalaTest (which can also run JUnit test
cases), both JUnit and this adapter are pure Java, so you can run JUnit tests
with any Scala version supported by sbt without having to build a
binary-compatible test framework first.

See LICENSE.txt for licensing conditions (BSD-style).

To use with sbt 0.7, add the following dependency to your project:

  val junitInterface = "com.novocode" % "junit-interface" % "0.6" % "test->default"

JUnit itself is automatically pulled in as a transitive dependency. sbt
already knows about junit-interface so the dependency alone is enough. You do
not have to add it to the list of test frameworks.

The following options are supported for JUnit tests:

  -v  Log "test run started" / "test started" / "test run finished" events on
      log level "info" instead of "debug".

  -q  Suppress stdout for successful tests. Stderr is printed to the console
      normally. Stdout is written to a buffer and discarded when a test
      succeeds. If it fails, the buffer is dumped to the console. Since stdio
      redirection in Java is a bad kludge (System.setOut() changes the static
      final field System.out through native code) this may not work for all
      scenarios. Scala has its own console with a sane redirection feature. If
      Scala is detected on the class path, junit-interface tries to reroute
      scala.Console's stdout, too.

  +v  Turn off -v. Takes precedence over -v.

  +q  Turn off -q. Takes precedence over -q.

  -tests=<REGEXPS>  Run only the tests whose names match one of the specified
      regular expressions (in a comma-separated list). Non-matched tests are
      ignored. Only individual test cases are matched, not test classes. Use
      sbt's "test-only" command instead to match test classes.

You can set default options in your project:

  override def testOptions = 
    super.testOptions ++ 
    Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))

Or use them with the test-quick and test-only commands:

  test-only -- +q +v

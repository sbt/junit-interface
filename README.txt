An implementation of sbt's test interface <http://github.com/harrah/test-interface>
for JUnit 4. This allows you to run JUnit <http://www.junit.org/> tests from sbt.

Unlike Scala testing frameworks like ScalaTest (which can also run JUnit test
cases), both JUnit and this adapter are pure Java, so you can run JUnit tests
with any Scala version supported by sbt without having to build a
binary-compatible test framework first.

See LICENSE.txt for licensing conditions (BSD-style).

To use with sbt 0.10+, add the following dependency to your build.sbt:

  libraryDependencies += "com.novocode" % "junit-interface" % "0.10-M1" % "test"

To use with sbt 0.7, add the following dependency to your project:

  val junitInterface = "com.novocode" % "junit-interface" % "0.10-M1" % "test"

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

  -n  Do not use ANSI colors in the output even if sbt reports that they are
      supported. 

  -s  Try to decode Scala names in stack traces and test names. Fall back
      silently to non-decoded names if no matching Scala library is on the
      class path.

  -a  Log stack traces for AssertionErrors (thrown by all assert* methods in
      JUnit). Without these options, failed assertions do not print a stack
      trace.

  +v  Turn off -v. Takes precedence over -v.

  +q  Turn off -q. Takes precedence over -q.

  +n  Turn off -n. Takes precedence over -n.

  +s  Turn off -s. Takes precedence over -s.

  +a  Turn off -a. Takes precedence over -a.

  -tests=<REGEXPS>  Run only the tests whose names match one of the specified
      regular expressions (in a comma-separated list). Non-matched tests are
      ignored. Only individual test cases are matched, not test classes. Use
      sbt's "test-only" command instead to match test classes.

  -Dkey=value Temporarily set a system property for the duration of the test
      run. The property is restored to its previous value after the test has
      ended. Note that system properties are global to the entire JVM and they
      can be modified in a non-transactional way, so you should run tests
      serially and not perform any other tasks in parallel which depend on
      the modified property.

  Any parameter not starting with - or + is treated as a glob pattern for
  matching tests. Unlike the patterns given directly to sbt's "test-only"
  command, the patterns given to junit-interface will match against the full
  test names (as displayed by junit-interface) of all atomic test cases, so
  you can match on test methods and parts of suites with custom runners.

In sbt 0.10+, you can set default options in your build.sbt file:

  testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

In sbt 0.7, add the following to your project:

  override def testOptions = 
    super.testOptions ++ 
    Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))

Or use them with the test-quick and test-only commands:

  test-only -- +q +v *Sequence*h2mem*

# JUnit Interface

JUnit Interface is an implementation of [sbt's test interface](https://github.com/sbt/test-interface) for [JUnit 4](https://junit.org/junit4/). This allows you to run JUnit tests from [sbt](http://www.scala-sbt.org/). For JUnit 5, check out [maichler/sbt-jupiter-interface](https://github.com/maichler/sbt-jupiter-interface).

Unlike Scala testing frameworks like ScalaTest (which can also run JUnit test cases), both JUnit and this adapter are pure Java, so you can run JUnit tests with any Scala version supported by sbt without having to build a binary-compatible test framework first.

### Setup

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.2" % Test
```

**Note**: Organization name has changed to **`"com.github.sbt"`** in 0.12.

JUnit itself is automatically pulled in as a transitive dependency.

 junit-interface version  | JUnit version
:-------------------------|:--------------
 0.13.2                   | 4.13.2
 0.13.1                   | 4.13.1
 0.13                     | 4.13
 0.12                     | 4.12

sbt already knows about junit-interface so the dependency alone is enough. You do not have to add it to the list of test frameworks.

### Usage

To run the JUnit tests, type in `test` in the sbt shell:

```
> test
```

To run the tests incrementally, and run only the failed tests since the last run, type in `testQuick`:

```
> testQuick
```

To run only a specific test suite, use `testOnly <class name glob>`:

```
> testOnly example.HelloTest
```

You can use a glob pattern instead as:

```
> testOnly *.HelloTest
```

To run only a specific test example (a method) within a test suite, use `testOnly -- <test name>`:

```
> testOnly -- example.HelloTest.testSuccess1
print from testSuccess1
[info] Passed: Total 1, Failed 0, Errors 0, Passed 1
```

You can use the glob pattern here as well:

```
> testOnly -- *.HelloTest.testI*
[info] Test example.HelloTest.testIgnored1 ignored
[info] Test example.HelloTest.testIgnored2 ignored
[info] Test example.HelloTest.testIgnored3 ignored
```

Note: Any arguments after `--` are treated as [test framework argument](https://www.scala-sbt.org/1.x/docs/Testing.html#Test+Framework+Arguments), and they are passed to junit-interface. Because `test` task cannot take any arguments, we can use `testOnly` task to pass ad-hoc options.

To get run all tests with verbose output:

```
> testOnly -- -v
print from testSuccess1
[info] Test run example.HelloTest started
[info] Test example.HelloTest.testSuccess1 started
[info] Test example.HelloTest.testIgnored1 ignored
[info] Test example.HelloTest.testIgnored2 ignored
[info] Test example.HelloTest.testIgnored3 ignored
[info] Test run example.HelloTest finished: 0 failed, 3 ignored, 1 total, 0.005s
[info] Passed: Total 1, Failed 0, Errors 0, Passed 1, Ignored 3
```

The following options are supported for JUnit tests:

 Option                                       | Description
:---------------------------------------------|:----------------------
 `-v`                                         | Same as `--verbosity=2`
 `-q`                                         | Suppress stdout for successful tests. Stderr is printed to the console normally. Stdout is written to a buffer and discarded when a test succeeds. If it fails, the buffer is dumped to the console. Since stdio redirection in Java is a bad kludge (`System.setOut()` changes the static final field System.out through native code) this may not work for all scenarios. Scala has its own console with a sane redirection feature. If Scala is detected on the class path, junit-interface tries to reroute scala.Console's stdout, too.
 `-n`                                         | Do not use ANSI colors in the output even if sbt reports that they are supported.
 `-s`                                         | Try to decode Scala names in stack traces and test names. Fall back silently to non-decoded names if no matching Scala library is on the class path.
 `-a`                                         | Show stack traces and exception class name for AssertionErrors (thrown by all assert* methods in JUnit).`
 `-c`                                         | Do not print the exception class name prefix for any messages. With this option, only the result of getMessage() plus a stack trace is shown.
 `+v`                                         | Same as `--verbosity=0`
 `+q`                                         | Turn off `-q`. Takes precedence over `-q`.
 `+n`                                         | Turn off `-n`. Takes precedence over `-n`.
 `+s`                                         | Turn off `-s`. Takes precedence over `-s`.
 `+a`                                         | Turn off `-a`. Takes precedence over `-a`.
 `+c`                                         | Turn off `-c`. Takes precedence over `-c`.
 `--ignore-runners=<COMMA-SEPARATED-STRINGS>` | Ignore tests with a `@RunWith` annotation if the Runner class name is contained in this list. The default value is `org.junit.runners.Suite`.
 `--tests=<REGEXPS>`                          | Run only the tests whose names match one of the specified regular expressions (in a comma-separated list). Non-matched tests are ignored. Only individual test case names are matched, not test classes. Example: For test `MyClassTest.testBasic()` only "testBasic" is matched. Use sbt's `testOnly` task instead to match test classes.
 `-Dkey=value`                                | Temporarily set a system property for the duration of the test run. The property is restored to its previous value after the test has ended. Note that system properties are global to the entire JVM and they can be modified in a non-transactional way, so you should run tests serially and not perform any other tasks in parallel which depend on the modified property.
 `--run-listener=<CLASS_NAME>`                | A (user defined) class which extends `org.junit.runner.notification.RunListener`. An instance of this class is created and added to the JUnit Runner, so that it will receive the run events. For more information, see [RunListener](http://junit.org/javadoc/latest/org/junit/runner/notification/RunListener.html). *Note: this uses the test-classloader, so the class needs to be defined in `src/test` or `src/main` or included as a test or compile dependency*
 `--include-categories=<CLASSES>`             | A comma separated list of category class names that should be included. Only tests with one or more of these categories will be run.
 `--exclude-categories=<CLASSES>`             | A comma separated list of category class names that should be excluded. No tests that match one or more of these categories will be run.
 `--verbosity=<INT>`                          | Higher verbosity logs more events at level "info" instead of "debug". 0: Default; 1: "Test run finished" at info; 2: Also "test run started" and "test started" at info; 3: Also "test finished" at info.
 `--summary=<INT>`                            | The type of summary to show for a test task execution. 0: Leave to sbt (default); 1: One line; 2: Include list of failed tests

Any parameter not starting with `-` or `+` is treated as a glob pattern for matching tests. Unlike the patterns given directly to sbt's `testOnly` task, the patterns given to junit-interface will match against the full test names (as displayed by junit-interface) of all atomic test cases, so you can match on test methods and parts of suites with custom runners.

You can set default options in your build.sbt file:

```scala
testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
```

Or use them with the `testQuick` and `testOnly` commands:

```
> testOnly -- +q +v *Sequence*h2mem*
```

### Credits

- junit-interface was created by Stefan Zeiger @szeiger in 2009
- See [contributors](https://github.com/sbt/junit-interface/graphs/contributors) for the list of other contributors

### License

See LICENSE.txt for licensing conditions (BSD-style).

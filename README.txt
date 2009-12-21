An implementation of sbt's test interface <http://github.com/harrah/test-interface> for JUnit 4.

Unlike Scala testing frameworks like ScalaTest (which can also run JUnit test cases), both JUnit
and this adapter are pure Java, so you can run JUnit tests with any Scala version supported by
sbt without having to build a binary-compatible test framework first.

See LICENSE.txt for licensing conditions (BSD-style).

To use with xsbt 0.6, add the following dependency to your project:

  val junitInterface = "com.novocode" % "junit-interface" % "0.3"
  override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFramework"))

Your test classes need to implement the marker interface com.novocode.junit.TestMarker in order
to be discovered. It is sufficient to add this to a test suite that bundles all test cases.

If you want to avoid the dependency on TestMarker, replace "JUnitFramework" above with
"JUnitFrameworkNoMarker". This causes all top-level classes built from the test sources to be
recognized as potential test cases. Classes with a @SuiteClasses annotation are not passed to
JUnit to avoid running test cases twice.

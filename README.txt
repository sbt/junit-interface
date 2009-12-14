An implementation of sbt's test interface <http://github.com/harrah/test-interface>
(version 0.2) for JUnit 4.

See LICENSE.txt for licensing conditions (BSD-style).

To use with xsbt 0.6.6, clone this project, publish it locally
with "sbt update publish-local" and add the following to the definition
of the xsbt project you want to test:

  val junitInterface = "com.novocode" % "junit-interface" % "0.2"
  override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFramework"))

Your test classes need to implement the marker interface
com.novocode.junit.TestMarker in order to be discovered. It is
sufficient to add this to the test suite that bundles all test cases.

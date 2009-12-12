import sbt._

class JUnitInterfaceProject(info: ProjectInfo) extends DefaultProject(info)
{
  val junit = "junit" % "junit" % "4.7"
  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.2"
}

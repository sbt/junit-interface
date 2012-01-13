import sbt._
import java.io.File

class JUnitInterfaceProject(info: ProjectInfo) extends DefaultProject(info)
{
  val junit = "junit" % "junit" % "4.8.2"
  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5"
  override def javaCompileOptions =
    JavaCompileOption("-target") :: JavaCompileOption("1.5") ::
    JavaCompileOption("-source") :: JavaCompileOption("1.5") ::
    Nil

  /*********** Publishing ***********/
  val publishTo = Resolver.file("ScalaQuery Test Repo", new File("d:/temp/repo/"))
  //val publishTo = "Scala Tools Snapshots" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  //val publishTo = "Scala Tools Releases" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
  def specificSnapshotRepo =
    Resolver.url("scala-nightly").
    artifacts("http://scala-tools.org/repo-snapshots/[organization]/[module]/2.8.0-SNAPSHOT/[artifact]-[revision].[ext]").
    mavenStyle()
  val nightlyScala = ModuleConfiguration("org.scala-lang", "*", "2.8.0-.*", specificSnapshotRepo)
  override def deliverScalaDependencies = Nil
  override def disableCrossPaths = true
  override def managedStyle = ManagedStyle.Maven

  /*********** Extra meta-data for the POM ***********/
  override def pomExtra =
      (<name>JUnitInterface</name>
      <url>http://github.com/szeiger/junit-interface/</url>
      <inceptionYear>2009</inceptionYear>
      <description>An implementation of sbt's test interface for JUnit 4</description>
      <licenses>
        <license>
          <name>Two-clause BSD-style license</name>
          <url>http://github.com/szeiger/junit-interface/blob/master/LICENSE.txt</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <developers>
        <developer>
          <id>szeiger</id>
          <name>Stefan Zeiger</name>
          <timezone>+1</timezone>
          <email>szeiger [at] novocode.com</email>
        </developer>
      </developers>
      <scm>
        <url>http://github.com/szeiger/junit-interface/</url>
      </scm>)
}

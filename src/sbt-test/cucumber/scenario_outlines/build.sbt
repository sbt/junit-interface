name := "cucumber-test"

organization := "com.waioeka.sbt"

version := "0.0.4"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq (
        "org.scalatest" %% "scalatest" % "3.0.1" % "test",
        "io.cucumber" % "cucumber-core" % "2.0.0" % "test",
        "io.cucumber" %% "cucumber-scala" % "2.0.0" % "test",
        "io.cucumber" % "cucumber-jvm" % "2.0.0" % "test",
        "io.cucumber" % "cucumber-junit" % "2.0.0" % "test",
        "com.novocode" % "junit-interface" % "0.11" % "test")

def before() : Unit = { println("beforeAll") }
def after() : Unit = { println("afterAll") }


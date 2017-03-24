libraryDependencies ++= Seq(
  "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
)

resolvers += Resolver.typesafeIvyRepo("releases")

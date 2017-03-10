enablePlugins(ScalaJSPlugin)

organization := "com.me"
name := "playground-binding.scala"
version := "0.1.0"

developers := List(
  Developer(
    "ccamel",
    "杨Chris Camel",
    "camel.christophe@gmail.com",
    url("https://github.com/ccamel")
  )
)
startYear := Some(2017)

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.thoughtworks.binding" %%% "dom" % "latest.release",
  "org.scalatest" %%% "scalatest" % "3.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test" // FIXME: https://github.com/scalatest/scalatest/issues/911
)


persistLauncher := true
persistLauncher in test := false


addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


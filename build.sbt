import java.nio.file.Files.newDirectoryStream
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Path, Paths}
import java.util.function.Consumer

enablePlugins(ScalaJSPlugin)

organization := "com.me"
name := "playground-binding.scala"
version := "0.1.0"

developers := List(
  Developer(
    "ccamel",
    "Chris Camel",
    "camel.christophe@gmail.com",
    url("https://github.com/ccamel")
  )
)
startYear := Some(2017)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.3",
  "com.thoughtworks.binding" %%% "dom" % "11.0.0-M6",
  "com.thoughtworks.binding" %%% "route" % "11.0.0-M6",
  "org.scalatest" %%% "scalatest" % "3.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test" // FIXME: https://github.com/scalatest/scalatest/issues/911
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
scalaJSUseMainModuleInitializer := true

lazy val dist = taskKey[Unit]("Make distribution")

dist := {
  implicit def toPath (filename: String): Path = Paths.get(filename)

  val srcDir: Path = "target/scala-2.12"
  val destDir: Path = "dist"
  val jsDir = destDir.resolve("js")

  println(s"Making distribution to $destDir")

  newDirectoryStream(srcDir,"playground*.js").forEach( new Consumer[Path]() {
    override def accept(f: Path): Unit = {
      val dst = jsDir.resolve(f.getFileName)
      dst.toFile.mkdirs()
      Files.copy(f, dst, REPLACE_EXISTING)
    }
  } )
  Files.copy("index.html", destDir.resolve("index.html"), REPLACE_EXISTING)
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


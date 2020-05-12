import java.nio.file.Files.{copy, deleteIfExists, newDirectoryStream, walk}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Path, Paths}
import java.util.function.Consumer

import scala.collection.JavaConversions.asScalaIterator

enablePlugins(ScalaJSPlugin)

organization := "com.me"
name := "playground-binding.scala"

developers := List(
  Developer(
    "ccamel",
    "Chris Camel",
    "camel.christophe@gmail.com",
    url("https://github.com/ccamel")
  )
)
startYear := Some(2017)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.8",
  "org.scala-lang.modules" % "scala-xml_2.12" % "1.3.0",
  "com.thoughtworks.binding" %%% "dom" % "11.9.0",
  "com.thoughtworks.binding" %%% "route" % "11.9.0",
  "org.scalatest" %%% "scalatest" % "3.1.2" % "test",
  "org.scalatest" %% "scalatest" % "3.1.2" % "test" // FIXME: https://github.com/scalatest/scalatest/issues/911
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
scalaJSUseMainModuleInitializer := true

lazy val dist = taskKey[Unit]("Make distribution")

dist := {
  implicit def toPath(filename: String): Path = Paths.get(filename)

  val targetDir: Path = "target"
  val srcDir: Path = targetDir.resolve("scala-2.12")
  val distDir: Path = "dist"
  val jsDir = distDir.resolve("js")
  val archive = targetDir.resolve("playground-binding.scala.zip")

  streams.value.log.info(s"Make distribution to $distDir")

  newDirectoryStream(srcDir, "playground*.js").forEach(new Consumer[Path]() {
    override def accept(f: Path): Unit = {
      val dst = jsDir.resolve(f.getFileName)
      dst.toFile.mkdirs()
      copy(f, dst, REPLACE_EXISTING)
    }
  })
  copy("index.html", distDir.resolve("index.html"), REPLACE_EXISTING)

  streams.value.log.info(s"Create archive")

  deleteIfExists(archive)

  val files = walk(distDir)
    .iterator()
    .toTraversable
    .map { it => (it.toFile, distDir.relativize(it).toString) }

  sbt.IO.zip(files, archive.toFile)
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

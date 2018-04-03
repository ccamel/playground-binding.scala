import sbtrelease.ReleasePlugin.runtimeVersion

sourceGenerators in Compile += genCodeTask.taskValue

def generateAppInfoSourceFile(sourceManaged: java.io.File, version: String) = {
  val file = sourceManaged / "com" / "ccm" / "me" / "playground" / "bindingscala" / "AppInfo.scala"
  IO.write(file,
    s"""
       |package com.ccm.me.playground.bindingscala
       |
       |object AppInfo {
       |  val version = "$version"
       |}""".stripMargin)
  Set(file)
}

def genCodeTask = Def.task {
  streams.value.log.info("Generating sources")
  val cachedCompile = FileFunction.cached(
    streams.value.cacheDirectory / "code-gen",
    inStyle = FilesInfo.lastModified,
    outStyle = FilesInfo.exists) {
    (in: Set[java.io.File]) =>
      streams.value.log.info("Generating AppInfo")
      generateAppInfoSourceFile((sourceManaged in Compile).value, runtimeVersion.value)
  }
  cachedCompile(Set(file("build.sbt"))).toSeq
}

val generateSources = taskKey[List[File]]("generate sources")

generateSources := {
  (sourceGenerators in Compile) {
    _.join.map(_.flatten.toList)
  }
}.value

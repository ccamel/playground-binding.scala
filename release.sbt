import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleasePlugin.runtimeVersion

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // publishArtifacts,
  setNextVersion,
  commitNextVersion
  // pushChanges
)

releaseCommitMessage := s":arrow_up: bump version to ${runtimeVersion.value}"
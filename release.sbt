import sbtrelease.ReleasePlugin.runtimeVersion
import sbtrelease.ReleaseStateTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  pushChanges,
  // publishArtifacts,
  setNextVersion,
  commitNextVersion
  // pushChanges
)

releaseCommitMessage := s":arrow_up: bump version to ${runtimeVersion.value}"
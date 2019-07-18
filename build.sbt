organization in ThisBuild := "io.methvin.standardmethods"
organizationName in ThisBuild := "Greg Methvin"
startYear in ThisBuild := Some(2017)
licenses in ThisBuild := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html"))
homepage in ThisBuild := Some(url("https://github.com/gmethvin/standard-methods"))
scmInfo in ThisBuild := Some(
  ScmInfo(url("https://github.com/gmethvin/standard-methods"), "scm:git@github.com:gmethvin/standard-methods.git")
)
developers in ThisBuild := List(
  Developer("gmethvin", "Greg Methvin", "greg@methvin.net", new URL("https://github.com/gmethvin"))
)

scalaVersion in ThisBuild := "2.13.0"
crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.8", scalaVersion.value)

lazy val macros = (project in file("macros"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    )
  )

lazy val root = (project in file("."))
  .settings(
    PgpKeys.publishSigned := {},
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    skip in publish := true
  )
  .aggregate(macros)

publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

import ReleaseTransformations._
releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

scalafmtOnCompile in ThisBuild := true

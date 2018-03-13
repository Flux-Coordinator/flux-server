name := """flux-server"""
organization := "ch.hsr.flux"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

val guiceVersion = "4.2.0"
val guiceDeps = Seq(
  "com.google.inject" % "guice" % guiceVersion,
  "com.google.inject.extensions" % "guice-assistedinject" % guiceVersion
)

libraryDependencies ++= Seq(
  guice
) ++ guiceDeps

jacocoReportSettings := JacocoReportSettings()
  .withTitle("Flux Server JACOCO Report")
  .withFormats(JacocoReportFormats.XML)

jacocoExcludes := Seq("views*", "*Routes*", "controllers*routes*", "controllers*Reverse*", "controllers*javascript*", "controller*ref*")
jacocoDirectory := baseDirectory.value /"target/jacoco"
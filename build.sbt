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

//val appDependencies = Seq(
//  "uk.co.panaxiom" %% "play-jongo" % "2.1.0-jongo1.3",
//  "org.mongodb" % "mongo-java-driver" % "3.6.3"
//)

libraryDependencies ++= Seq(
  guice
) ++ guiceDeps

libraryDependencies ++= Seq(
  "org.mongodb" % "mongodb-driver" % "3.6.3"
)

jacocoReportSettings := JacocoReportSettings()
  .withTitle("Flux Server JACOCO Report")
  .withFormats(JacocoReportFormats.ScalaHTML)

jacocoExcludes := Seq("views*", "*Routes*", "controllers*routes*", "controllers*Reverse*", "controllers*javascript*", "controller*ref*")
jacocoDirectory := baseDirectory.value /"target/jacoco"


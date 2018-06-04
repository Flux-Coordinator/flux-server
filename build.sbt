name := """flux-server"""
organization := "ch.hsr.flux"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

val guiceVersion = "4.2.0"
val guiceDeps = Seq(
  "com.google.inject" % "guice" % guiceVersion,
  "com.google.inject.extensions" % "guice-assistedinject" % guiceVersion
)

libraryDependencies ++= Seq(
  guice,
  javaJpa,
  "org.postgresql" % "postgresql" % "42.2.2",
  "org.hibernate" % "hibernate-entitymanager" % "5.2.17.Final",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate5" % "2.9.5",
  "org.mindrot" % "jbcrypt" % "0.4"
) ++ guiceDeps

PlayKeys.externalizeResources := false

jacocoReportSettings := JacocoReportSettings()
  .withTitle("Flux Server JACOCO Report")
  .withFormats(JacocoReportFormats.ScalaHTML)

jacocoExcludes := Seq("views*", "*Routes*", "controllers*routes*", "controllers*Reverse*", "controllers*javascript*", "controller*ref*")
jacocoDirectory := baseDirectory.value /"target/jacoco"


name := """flux-server"""
organization := "ch.hsr.flux"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava, LauncherJarPlugin)
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
  "com.h2database" % "h2" % "1.4.192",
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

javaOptions in Test += "-Dconfig.file=conf/test.conf"

// Dont need the documentations built with the dist task
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false
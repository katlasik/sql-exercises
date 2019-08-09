name := """sql-exercises"""
organization := "pl.atk"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.13.0"

pipelineStages := Seq(gzip, digest)

libraryDependencies += guice
libraryDependencies += jdbc

libraryDependencies += "org.webjars" % "jquery" % "3.4.1"
libraryDependencies += "org.webjars" % "bootstrap" % "4.3.1"
libraryDependencies += "org.webjars" % "jasny-bootstrap" % "3.1.3"
libraryDependencies += "org.webjars" % "codemirror" % "5.45.0"
libraryDependencies += "org.webjars" % "font-awesome" % "5.9.0"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.11.1"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.16"

libraryDependencies += "com.github.katlasik" %% "functionmeta" % "0.2.3" % "provided"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

includeFilter in gzip := GlobFilter("**/*")

DigestKeys.algorithms += "sha1"

includeFilter in digest := GlobFilter("**/*")

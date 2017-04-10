name := "generatePOC"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.h2database" % "h2" % "1.3.176",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.2"
)

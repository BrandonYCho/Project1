ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.15"

lazy val root = (project in file("."))
  .settings(
    name := "Project1"
  )

val bCryptVersion = "4.3.0"
val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"
val scalajVersion = "2.4.2"
val sparkVersion = "3.1.2"
val scalaTestVersion = "3.2.7"

libraryDependencies ++= Seq(
  // BCrypt for Scala
  "com.github.t3hnar" %% "scala-bcrypt" % bCryptVersion,

  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.lihaoyi" %% "ujson" % "1.5.0",

  // Simplified Http / scalaj-http
  "org.scalaj" %% "scalaj-http" % scalajVersion,

  // Testing
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",

  // Spark, SQL, Hive
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion
)
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.15"

lazy val root = (project in file("."))
  .settings(
    name := "spark-app-stream"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.4.0",
  "org.apache.spark" %% "spark-sql" % "3.4.0",
  "org.apache.spark" %% "spark-mllib" % "3.4.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.4.0",
  "org.mongodb.spark" %% "mongo-spark-connector" % "3.0.0",
  "com.johnsnowlabs.nlp" %% "spark-nlp" % "5.4.0",

)

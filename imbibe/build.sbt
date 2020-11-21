import Dependencies._

ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "imbibe",
    libraryDependencies ++= Seq(
      scalaTest % Test,

      "dev.zio" %% "zio" % "1.0.0-RC18-2",
      "dev.zio" %% "zio-interop-cats" % "2.2.0.1",

      "org.http4s" %% "http4s-blaze-server" % "0.21.1",
      "org.http4s" %% "http4s-circe" % "0.21.1",
      "org.http4s" %% "http4s-dsl" % "0.21.1",
      "org.http4s" %% "http4s-circe" % "0.21.1",
      "ch.qos.logback" % "logback-classic" % "1.1.2",

      "io.circe" %% "circe-core" % "0.12.3",
      "io.circe" %% "circe-generic" % "0.12.3",
      "io.circe" %% "circe-optics" % "0.13.0",

      "org.tpolecat" %% "doobie-core" % "0.8.8",

      "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",

      "com.github.pureconfig" %% "pureconfig" % "0.14.0",

      "org.sangria-graphql" %% "sangria" % "2.0.0",
      "org.sangria-graphql" %% "sangria-circe" % "1.3.1"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

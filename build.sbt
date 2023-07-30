name := "pbyrne84-github-io"

scalaVersion := "3.3.0"

parallelExecution in Test := false

libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"
libraryDependencies += "dev.zio" %% "zio" % "2.0.15"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test

scalacOptions ++= Seq(
  "-no-indent" // disable the evil whitespace
)

scalaVersion := "2.13.10"

scalacOptions ++= Seq(
  "-feature",
  "-language:reflectiveCalls",
)

// Chisel 3.5
addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.6.0" cross CrossVersion.full)
libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.6.0"
libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "0.6.0"

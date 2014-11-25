scalaVersion := "2.11.1"

mainClass in (Compile,run) := Some("CleanDb")

libraryDependencies ++= Seq(
	"com.typesafe.slick" %% "slick" % "2.1.0",
	"mysql" % "mysql-connector-java" % "5.1.31",
	"commons-io" % "commons-io" % "2.4"
)
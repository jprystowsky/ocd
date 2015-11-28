name := "ocd"

version := "0.0.1"

scalaVersion := "2.11.7"

resolvers += Resolver.sonatypeRepo("public")
resolvers ++= Seq("RoundEights" at "http://maven.spikemark.net/roundeights")

libraryDependencies ++= Seq(
	"commons-codec" % "commons-codec" % "1.10",
	"com.github.scopt" %% "scopt" % "3.3.0",
	"com.roundeights" %% "hasher" % "1.2.0"
)
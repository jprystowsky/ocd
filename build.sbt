name := "ocd"

version := "0.0.1"

scalaVersion := "2.11.7"

resolvers += Resolver.sonatypeRepo("public")
resolvers ++= Seq("RoundEights" at "http://maven.spikemark.net/roundeights")

libraryDependencies ++= Seq(
	"com.github.scopt" %% "scopt" % "3.3.0",
	"com.roundeights" %% "hasher" % "1.2.0",
	"org.json4s" % "json4s-native_2.11" % "3.3.0",
	"org.json4s" % "json4s-jackson_2.11" % "3.3.0"
)
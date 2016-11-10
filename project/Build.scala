import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "funky"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here
    "javax.mail" % "mail" % "1.4",
    "org.apache.commons" % "commons-email" % "1.3.2",
    "com.google.gdata" % "core" % "1.47.1",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "it,test",  //Added for it:test, but needed for test also
    "com.typesafe.play" %% "play-test" % "2.2.3" % "it,test"      //Added for it:test
  )

  //Gives integration tests (it:test) the same settings as test, removing this
  // gives a lot more logger output for example
  lazy val IntegrationTest = config("it") extend(Test)

  val main = play.Project(appName, appVersion, appDependencies)
    .configs( IntegrationTest )  //Added for it:test
    .settings(Defaults.itSettings : _*)  //Added for it:test
    .settings(scalaSource in IntegrationTest <<= baseDirectory(_ / "it"))  //Added for it:test
    .settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId"
    )

}
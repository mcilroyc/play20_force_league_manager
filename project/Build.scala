import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "adiabats-manager"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "redis.clients" % "jedis" % "2.0.0",
      "com.force.api" % "force-rest-api" % "0.0.19",
      "com.typesafe" %% "play-plugins-redis" % "2.0.2",
      "org.sedis" %% "sedis" % "1.0.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
     resolvers += "force-rest-api repository on GitHub" at "http://jesperfj.github.com/force-rest-api/repository/",
     resolvers += "Sedis repository" at "http://guice-maven.googlecode.com/svn/trunk"
    )

}

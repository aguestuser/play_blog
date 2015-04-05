name := "silly_blog"

version := "1.0"

lazy val `silly_blog` = (project in file(".")).enablePlugins(PlayScala).enablePlugins(SbtWeb)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies ++= Seq("org.postgresql" % "postgresql" % "9.4-1200-jdbc41")

libraryDependencies ++= Seq("org.scalaz" %% "scalaz-core" % "7.0.6")

libraryDependencies ++= Seq("com.github.nscala-time" %% "nscala-time" % "1.8.0")

libraryDependencies ++= Seq("org.webjars" % "bootstrap" % "3.0.2")

libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-simple")) }

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

herokuAppName in Compile := "sillyblog"
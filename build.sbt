name := "silly_blog"

version := "1.0"

lazy val `silly_blog` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
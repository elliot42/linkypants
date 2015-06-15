lazy val root = (project in file(".")).
settings(
    name := "wingnut",

    version := "1.0",

    scalaVersion := "2.11.4",

    libraryDependencies += "javax.servlet" % "servlet-api" % "2.5",

    libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.3.0.M2",

    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.5"
    )

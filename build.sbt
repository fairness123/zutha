name := "zutha"

version := "0.0.1-SNAPSHOT"

organization := "net.zutha"

scalaVersion := "2.9.0-1"

seq(webSettings :_*)

//for JRebel
jettyScanDirs := Nil
  
resolvers ++= Seq(
    "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
    "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
    "tmlab" at "http://maven.topicmapslab.de/public",
    "org.tmapi" at "http://www.tmapi.org/maven-repository/snapshots",
    "semagia" at "http://repository.semagia.com/snapshots"    
)

libraryDependencies ++= {
    val liftVersion = "2.4-M1"
    val tmql4j_version = "3.2.0-SNAPSHOT"
    val majortom_version = "1.2.0"
    Seq(
	"org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "jetty",
	"ch.qos.logback" % "logback-classic" % "0.9.26",
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "de.topicmapslab.tmql4j" % "tmql4j-path" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-draft2010" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-draft2011" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-delete" % tmql4j_version withJavadoc() withSources(),
	"de.topicmapslab.tmql4j" % "tmql4j-flwr" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-insert" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-merge" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-select" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-template" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-update" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-tolog" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.tmql4j" % "tmql4j-majortom" % tmql4j_version withJavadoc() withSources(),
    "de.topicmapslab.majortom" % "majortom-model" % majortom_version withJavadoc() withSources(),
    "de.topicmapslab.majortom" % "majortom-db" % majortom_version withJavadoc() withSources(),
    "de.topicmapslab.majortom" % "majortom-inMemory" % majortom_version withJavadoc() withSources(),
    "de.topicmapslab.majortom" % "majortom-queued" % majortom_version withJavadoc() withSources()
)}

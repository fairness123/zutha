resolvers ++= Seq(
    "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
    "Web plugin repo" at "http://siasia.github.com/maven2",
	Resolver.url("Typesafe repository", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns)
)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.10"))

//Sbt Idea
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

//start-script-plugin
resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.1")

// OneJar
//resolvers += "retronym-releases" at "http://retronym.github.com/repo/releases"

//resolvers += "retronym-snapshots" at "http://retronym.github.com/repo/snapshots"

//addSbtPlugin("com.github.retronym" % "sbt-onejar" % "0.6")


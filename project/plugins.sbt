resolvers ++= Seq(
  "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
	"Web plugin repo" at "http://siasia.github.com/maven2",
	Resolver.url("Typesafe repository", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns)
)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.8"))

//resolvers += "sonatype.repo" at "https://oss.sonatype.org/content/groups/public"

//libraryDependencies <+= sbtVersion(v => "eu.getintheloop" %% "sbt-cloudbees-plugin" % ("0.3.1_"+v))

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

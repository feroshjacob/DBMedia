 name := "DBMedia"
 
 version := "0.0.1"
 
 organization := "com.recipegrace"


scalaVersion := "2.11.4"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))



libraryDependencies ++= Seq(
   "org.dbpedia.extraction" % "core" % "4.0-SNAPSHOT",
     "org.scalaj" %% "scalaj-http" % "1.1.0",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test") 

resolvers ++= Seq( 
                   "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
                  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
                 "CB dataservices" at "http://bigdata.careerbuilder.com/archiva/repository/dataservices/"
               )


resourceDirectory in Compile := baseDirectory.value / "files"

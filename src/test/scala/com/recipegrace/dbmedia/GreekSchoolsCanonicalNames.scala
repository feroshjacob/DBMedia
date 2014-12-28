package cb.wiki.mediawiki

import scala.io.Source

object GreekSchoolsCanonicalNames {
  
  def main(args: Array[String]) {
 

  val lines = Source.fromFile("files/netherlandSchools.txt").getLines;
  val titles = lines
 
    .map(f => f.split("\\t", -1))
    .map(f => {
      val finalResult = if (f(0) != "CB") {
        val nativeName = MediaWikiHelper.getPropertyInPage(f(1), "nativeName") match {
          case Some(x) => if(x.trim().length > 0) x else f(1)
          case _ => f(1)

        }
        List(f(0), nativeName, f(2), f(3))
      } else {
        List(f(0), f(1), f(2), f(3))
      }
      finalResult.mkString("\t")
    }).toList

  titles.foreach(f => println(f))
  }
}   
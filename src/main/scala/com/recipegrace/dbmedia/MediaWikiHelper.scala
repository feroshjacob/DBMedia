package com.recipegrace.dbmedia
 

import org.dbpedia.extraction.destinations.Quad
import scala.util.Try

object MediaWikiHelper extends WikiFunctions {  

  def getAllPropertiesInPage(wikiTitle: String) = { 
    val page = wikiTitleSearch(wikiTitle) 
    def quadParser(quads: Seq[Quad]) = {
      quads.map(p => {
        if (p.dataset.equals("infobox_properties") && p.predicate.startsWith("http://en.dbpedia.org/property/"))
          (p.predicate.split("""http:\/\/en\.dbpedia\.org\/property\/""")(1), p.value)
        else ("", "")
      })
        .filter(p => p._1 != "")
    }
    DBPediaHelper.getIBProperties(page, quadParser _)
  }
  def getPropertyInPage(wikiTitle: String, property: String) = {
    val page = wikiTitleSearch(wikiTitle)
    def quadParser(quads: Seq[Quad]) = {
      quads.foldLeft("")((p, q) => {
        if (q.dataset.equals("infobox_properties") && q.predicate == s"http://en.dbpedia.org/property/$property")
          q.value
        else p
      })

    }
     DBPediaHelper.getIBProperties(page, quadParser _) 
  }

}

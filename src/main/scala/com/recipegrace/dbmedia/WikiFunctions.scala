package com.recipegrace.dbmedia

import scalaj.http.Http
import scala.xml.Elem
import scalaj.http.HttpOptions
import scala.xml.XML
trait WikiFunctions {

  private val params = Map(
    "format" -> "xml",
    "action" -> "query",
    "prop" -> "revisions",
    "rvprop" -> "content")
  private def tranformXML(webXML: Elem) = {
    val rev = webXML \\ "rev"
  
    val wikiText = if(rev.isEmpty) <rev></rev>else  rev(0)
    <mediawiki>
      <page>
        <title>""</title>
        <ns>0</ns>
        <id>0</id>
        <revision>
          <id>0</id>
          <parentid>0</parentid>
          <minor/>
          <comment/>
          <text xml:space="preserve">
            { wikiText.text }
          </text>
        </revision>
      </page>
    </mediawiki>
  } 
  def wikiTitleSearch(query: String) = {

    val finalParams = params + ("titles" -> query)
    val request = Http("http://en.wikipedia.org/w/api.php")
      .params(finalParams)
      .charset("ISO-8859-1")
      .options(HttpOptions.connTimeout(5000), HttpOptions.readTimeout(5000))
    val xmlData = XML.loadString(request.asString.body)
    tranformXML(xmlData)
  }

}

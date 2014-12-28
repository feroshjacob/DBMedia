package com.recipegrace.dbmedia

import scalaj.http.Http
import java.net.URLEncoder
import scalaj.http.HttpOptions
import cb.wiki.helpers.CBInfoBoxParser._
import cb.wiki.helpers.DBPediaHelper
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers


class MediaWikiPropertyTest extends FunSuite with ShouldMatchers{


  test("Infobox test clarkson university") {

    val title = "Clarkson University"
    val propertyAndVal = ("imageName","Clarkson-seal.png" )
    val imageName = MediaWikiHelper. getPropertyInPage(title, propertyAndVal._1)
    imageName.get should be equals(propertyAndVal._2)
    val quads = MediaWikiHelper. getAllPropertiesInPage(title)
    quads.get should contain(propertyAndVal)
    
  }
  
}

package com.recipegrace.dbmedia

import org.scalatest.FunSuite
import org.scalatest.Matchers
import cb.wiki.helpers.CBInfoBoxParser._ 	
import org.dbpedia.extraction.destinations.Quad

class IBExtractorTest extends FunSuite with Matchers with TestHelper{

  
     def hasProperty(x:String, list:Seq[Quad]) = {
     !(  list
      .filter(p => p.dataset.equals("infobox_properties") && p.predicate == s"http://en.dbpedia.org/property/$x")
      .isEmpty)
      
     }
    test(" Valid Infobox test ") {
    val  quads =extractIB(wikiText1)
         
          hasProperty("industry", quads) should be (true)
          hasProperty("name", quads) should be (true)
          hasProperty("country", quads) should be (true)
          hasProperty("director", quads) should be (true)
          hasProperty("producer", quads) should be(true)
          hasProperty("alt", quads) should not be(true)
           hasProperty("image", quads) should not be(true)
             hasProperty("caption", quads) should not be true
    
    }
   test("Empty InfoBox test ") {
    val  quads =extractIB(wikiText2)
         
         quads.isEmpty should be (true)
         
    
    }
   

}

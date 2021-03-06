package com.recipegrace.dbmedia

import org.scalatest.FunSuite

import com.recipegrace.dbmedia.extractors.StandAloneExtractor._
class DisAmbigLinksExtractorTest extends  TestHelper{
  

    test("UAB disambig test ") {
    val  categories =extractDisAmbigLinks(wikiText5).getOrElse(("", List()))
    categories._1 should  equal("UAB")
    categories._2 should  have size  (8)
    categories._2 should contain ("University of Alabama at Birmingham")
  
   
    
    }
    test("Columbia university disambig test") {
    val  categories =extractDisAmbigLinks(wikiText6).getOrElse(("", List()))
    categories._1 should  equal("Columbia University".toUpperCase())
    categories._2 should  have size  (2)
    println(categories._2)
    categories._2 should contain ("Columbia College, Columbia University")
    categories._2 should contain ("Columbia University")
    
    }


}

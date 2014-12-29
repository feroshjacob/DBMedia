package com.recipegrace.dbmedia

import org.scalatest.FunSuite

import com.recipegrace.dbmedia.DBPediaHelper._
class CategoryExtractorTest extends TestHelper{
  

    test("Industry Valid Category test ") {
    val  categories =extractCategories(wikiText1,true).getOrElse(("", List()))
    categories._1 should equal  ("332")
     categories._2 should contain  ("1996 films")
     categories._2 should contain  ( "1990s drama films")
     categories._2 should contain  ( "Spanish films")
    
    }
   test("Industry Empty Category test ") {
      val  categories =extractCategories(wikiText2, true).getOrElse(("", List()))
    categories._1 should equal  ("333")
     categories._2 should equal (List())
     
    
    }

} 

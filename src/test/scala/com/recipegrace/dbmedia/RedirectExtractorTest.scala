package com.recipegrace.dbmedia

import org.scalatest.FunSuite

import com.recipegrace.dbmedia.DBPediaHelper._

class RedirectExtractorTest extends  TestHelper{
  

    test(" Valid Redirect test ") {
    val  redirects =extractRedirect(wikiText3).getOrElse(("", "",""))
     redirects._1 should equal  ("325")
     redirects._2 should equal  ("Action Film")
     redirects._3 should equal  ( "Action films")
    
    }
   test("Empty redirect test 1") { 
   
      val  redirects =extractRedirect(wikiText1).getOrElse(("", "",""))
     
     redirects._1 should equal  ("332")
     redirects._2 should equal  ("Actrius") 
     redirects._3 should equal  ( "")
    
    }
test("Empty redirect test 2") {
   
      val  redirects =extractRedirect(wikiText2).getOrElse(("", "",""))
     
     redirects._1 should equal  ("333")
     redirects._2 should equal  ("Actrius")
     redirects._3 should equal  ( "")
    
    }
test("Ignorecase redirect test 2") {
   
      val  redirects =extractRedirect(wikiText4).getOrElse(("", "",""))
     
     redirects._1 should equal  ("326")
     redirects._2 should equal  ("Action Film")
     redirects._3 should equal  ( "")
    
    }
}

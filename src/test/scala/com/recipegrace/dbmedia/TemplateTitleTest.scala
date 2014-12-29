package com.recipegrace.dbmedia

import org.scalatest.FunSuite
import org.scalatest.Matchers
import com.recipegrace.dbmedia.DBPediaHelper._

class TemplateTitleTest  extends  TestHelper{
  

  

    test(" Valid Template test ") {
    val  titles =extractTemplates(wikiText1).getOrElse(("",List()))
    titles._1 should equal  ("332")
    titles._2 should contain  ("infobox film")
    titles._2 should contain  ( "1990s-drama-film-stub")
    titles._2 should contain  ( "spain-film-stub")
     
    }
   test(" Empty Template test ") {
      val  titles =extractTemplates(wikiText2).getOrElse(("",List()))
     titles._1 should equal  ("333")
     titles._2 should equal (List())
     
    
    }


}

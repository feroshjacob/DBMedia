package com.recipegrace.dbmedia

import scala.collection.mutable.MutableList
import org.jsoup.Jsoup
import scala.util.matching.Regex
 
object StringProcessor {



  def getAllcombinations(text: String): List[String] = {
    val textArray = text.split("\\s")
    val total: List[Int] = (0 to textArray.length - 1).toList

    var allCombinations = List[List[Int]]()
    def createText(perms: List[Int]): String = {
      var modifiedText = MutableList[String]()
      for (each <- total) {
        def makeCaps(each: Int, perms: List[Int]) = {
          val converted: String = if (perms.contains(each)) textArray(each).capitalize else textArray(each).toLowerCase()
          converted
        }

        val text: String = makeCaps(each, perms)
        modifiedText = modifiedText ++ List(text)
      }
      modifiedText.mkString(" ")
    }
    val listResults = textArray.length match {
      case x if x > 5 => List[String]()
      case _ => {
        for (iter <- 1 to textArray.length) {
          val tempList: List[List[Int]] = for (i <- total.combinations(iter).toList)
            yield i
          allCombinations = allCombinations ++ tempList
        }

        val result = for (each <- allCombinations.slice(0, 47))
          yield createText(each)
        result
      }

    }

    val finalResult = listResults.toList
    finalResult ++ List(text.toUpperCase(), text.toLowerCase())
  }
  
    val Regex = """[ \~\`\!\@\#\$\%\^\&\*\(\)\-\_\+\=\{\}\[\]\|\\\/\:\;\"\'\<\>\,\.\?]*"""
  val NonSpaceSymbols = """[\~\`\!\@\#\$\%\^\&\*\(\)\-\_\+\=\{\}\[\]\|\\\/\:\;\"\'\<\>\,\.\?]"""
  val InverseRegex = """[^\~\`\!\@\#\$\%\^\&\*\(\)\_\+\=\{\}\[\]\|\\\/\:\;\"\'\<\>\.\?]*"""
  def isJustSymbolsOrEmpty(line: String): Boolean = {
    if ((line.trim()) matches Regex) true else false
  }
    
  
  def removeIfIncluded(line: String) = {
    val lower = line.toLowerCase()
    val returnValue = if (getAllcombinations(lower).contains(line)) lower else line
    returnValue
  }
  
  def removeLeadAndLagSymbols(line: String): String = {
    val LeadLagRegex = (Regex + "(" + InverseRegex + ")" + Regex).r
    line.trim match {
      case LeadLagRegex(value) => value
      case _ => line.trim()
    }
   // removeIfIncluded(replaceTabs(removedLine))
  }
  
  def replaceTabs(line:String):String= {
    
    doStrOperation(_.replaceAll("\\s+", " "),line)
  }
  def clean(line:String) = {
     doStrOperation(x=> removeLeadAndLagSymbols( replaceTabs(x).toLowerCase()), line)
  }
  def clean1(line:String) = {
     doStrOperation(x=> removeLeadAndLagSymbols( replaceTabs(x).toLowerCase()), line)
  }
  def normalize(line:String) = {
     doStrOperation(x=> trim(replaceTabs(x).toLowerCase()), line)
  }
    def removeXMLComments(line:String) = {
       doStrOperation( _.replaceAll("<!--.*?-->", "").trim, line)
     }
    
   def stripHTML(text:String) = {
    doStrOperation( Jsoup.parse(_).text(), text)
   }

   def trim(str:String) = {
     doStrOperation(_.trim, str)
   }
   def removeAmbersands(str:String) = {
     doStrOperation(_.replaceAll("\\&amp;", "\\&"), str)
   }
   
  private def doStrOperation(f:(String)=>String, str:String) = {
      str match {
        case x:String => f(str)
        case _ => ""
      }
     
   }
    def matches(s:String,expression: Regex): Boolean = {
      expression.findFirstIn(s) match {
        case Some(_) => true;
        case _ => false
      }
    }


}
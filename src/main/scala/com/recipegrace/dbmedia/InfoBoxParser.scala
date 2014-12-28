package com.recipegrace.dbmedia



import org.jsoup.Jsoup
import com.recipegrace.dbmedia.StringProcessor._

object InfoBoxParser {

  def getDoubleBracketParts(x: String): List[String] = {

    val DoubleBracket = """\[\[([^\[]*)\]\]""".r
    if (x.contains("[")) (for (DoubleBracket(part) <- DoubleBracket findAllIn x.trim) yield part).toList.flatMap(f => f.split("\\|"))
    else x.trim :: Nil

  }

  def getCommaSeperatedParts(x: String): List[String] = {
    x.split(",").toList.flatMap(f => {
      getDoubleBracketParts(f)
    })
  }

  def getDoubleCurlyBracketParts(x: String): List[String] = {
    val UBLList = """\{\{\s*ubl\s*\|(.*)\}\}""".r
    (for (UBLList(part) <- UBLList findAllIn x) yield part).toList.flatMap(f => getDoubleBracketParts(f))
  }

  def getProperty(properties: Map[String, String], property:String) = {
    properties.get(property) match {
      case Some(x: String) => {
      (getCommaSeperatedParts(x) ++ getDoubleCurlyBracketParts(x)).filter(p => p.length() > 0)
      }
      case _ => List()
    }
  }

  def getFirstProperty(text: String) = {
    if (text == null) ""
    else {
      val PropertyPattern = """.*[\{\[][\{\[](.*)[\}\]][\}\]].*""".r
      val stripped = Jsoup.parse(text).text()
      stripped match {
        case PropertyPattern(r) => r.split("\\|")(0)
        case _ => stripped
      }
    }
  }
  val Pattern = """.*\{\{\s*[Ii][Nn][Ff][Oo][Bb][Oo][Xx](.*)\}\}.*""".r
  def getInfoBoxProperties(content: String): (String, String, String, String) = {

    val properties = getInfoxBoxContents(content)

    (properties.getOrElse("infoBoxType", ""),
      properties.getOrElse("city", ""), properties.getOrElse("state", ""),
      properties.getOrElse("country", ""))

  }

  def getInfoxBoxContents(in: String): Map[String, String] = {
    in match {
      case Pattern(info) => getProperties(info)
      case _ => Map()
    }
  }

  def getProperties(input: String): Map[String, String] = { 
    val parsed = removeXMLComments(input).split("""\|""")
    var newProperties: List[String] = List[String]()
    for (item <- parsed) {
      if ((item.contains("]]")) && ((!item.contains("[["))
        || item.lastIndexOf("]]") < item.lastIndexOf("[["))) {
        newProperties = if (newProperties.isEmpty) item :: Nil
        else {
          val lastItem = newProperties.head.trim
          val updated = newProperties.diff(List(lastItem))
          (lastItem + "|" + item) :: updated
        }
      } else newProperties = item.trim :: newProperties
    }

    val result = for {
      i <- newProperties
      if (i.split("=").length > 1)
    } yield clean(i.split("=")(0)) -> getFirstProperty(clean(i.split("=")(1)))

    result.toMap ++ Map("infoBoxType" -> clean(newProperties.last))

  }
}

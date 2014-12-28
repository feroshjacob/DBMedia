package com.recipegrace.dbmedia

import scala.collection.mutable.Stack
import scala.xml.Elem
import scala.xml.Node
import scala.xml.NodeSeq

object WikiParseHelper {
  def getFirstLine(text: String): String = {
    type StackType = (Stack[String], String)
    val init: StackType = (Stack(), "")

    def postProcess(x: String) = {

      val text = x.replaceAll("\\([^\\(]*\\)", "").replaceAll("\\{\\s*\\{[^\\{]*\\}\\s*\\}", "")
      //  println(text.size)
      val result = if (text == null || text.split("\\.").isEmpty) x
      else if (text.split("\\.")(0).size > 100)
        text.split("\\.")(0) else text

      result.replaceAll("""[^aA-zZ]ref([^(ref)])\/?(ref|)""", " ")
    }
    def convert(p: StackType, q: String): StackType = {

      val total = q.count(_ == '{') - q.count(_ == '}')
      if (p._2 != "") p
      else if (p._1.isEmpty && q.trim.length() > 0 && !q.trim.startsWith("{")) (p._1, q)
      else if (total > 0) {
        for (i <- 1 to total) p._1.push("{")
        (p._1, "")

      } else if (total < 0) {
        for (i <- total to 1) if (!p._1.isEmpty) p._1.pop
        (p._1, "")

      } else {
        (p._1, "")
      }
    }
    def removeBracketOnly(x: String): Boolean = {
      if ((x.trim.startsWith("[[") && (x.trim.endsWith("]]")))) false
      else if (x.trim.startsWith("|")) false
      else true

    }

    //println(text)
    //val content = if(text.length() > 10000) text.substring(0,10000) else text
    val acc1 = text.replaceAll("(?s)\\!--.*?--", "")
    //  if(acc1.length()== text.length()) println("NO replacement" + pageId)
    val acc2 = acc1.split("\\r?\\n").toList.filter(f => f.trim.startsWith("'''"))
    if (acc2.isEmpty) {
      val acc3 = acc1.split("\\r?\\n").toList.filter(removeBracketOnly).foldLeft(init)((p, q) => convert(p, q))
      postProcess(acc3._2)
    } else postProcess(acc2(0))

  }
  def extractFirstLine(x: Elem): Option[(String, String)] = {

    val id = getNode(x, "revision", "id")
    val comment = getNode(x, "revision", "text")

    if (id.length() > 0)
      Some((id, getFirstLine(comment)))
    else None
  }
  def getNode(element: Elem, nodes: String*): String = {

    var varNode: Option[Node] = getFirstNonEmpty(element \ nodes.head)

    for (n <- nodes.tail) {
      // println("n is"+ n + " and node" + varNode)
      varNode = varNode match {
        case Some(x) => getFirstNonEmpty(x \ n)
        case _ => None
      }

    }
    varNode match {
      case Some(x) => x.text
      case _ => ""
    }

  }
  def getFirstNonEmpty(nodes: NodeSeq): Option[Node] = {
    //   println(nodes)
    if (nodes.isEmpty) None
    else Some(nodes(0))
  }

}

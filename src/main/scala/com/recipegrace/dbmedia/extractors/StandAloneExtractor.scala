package com.recipegrace.dbmedia.extractors

import org.dbpedia.extraction.wikiparser._
import org.dbpedia.extraction.sources.XMLSource
import org.dbpedia.extraction.destinations.Quad
import org.dbpedia.extraction.ontology.io.OntologyReader 
import org.dbpedia.extraction.util.Language
import scala.collection.mutable.ArrayBuffer
import org.dbpedia.extraction.mappings.InfoboxExtractor
import org.dbpedia.extraction.mappings.Redirects
import org.dbpedia.extraction.wikiparser.impl.simple.SimpleWikiParser
import org.dbpedia.extraction.mappings.PageContext 
import scala.xml.Elem
import org.dbpedia.extraction.sources.WikiPage
import scala.xml.XML
import org.dbpedia.extraction.config.mappings.DisambiguationExtractorConfig
 
object StandAloneExtractor extends BaseStandaloneExtractor {
  val context = new {
    def ontology = {
      val in = getClass().getResourceAsStream("/ontology.xml");

      val xmlElem = XML.load(in)

      val ontologySource = XMLSource.fromXML(xmlElem, Language.Mappings)
      new OntologyReader().read(ontologySource)
    }
    def language = Language.English
    def redirects = new Redirects(Map())
  }
  val parser = new SimpleWikiParser()
  private def getIBProperties[T](page: WikiPage, ftn: (Seq[Quad]) => T): Option[T] = {
    val extractor = new InfoboxExtractor(context)

    parser(page) match {
      case Some(n) => {
        //   extractor.g
        val quads = extractor.extract(n, page.title.resourceIri, new PageContext())
        Some(ftn(quads))

      }
      case _ => None

    }
  }
  def getIBProperties[T](elem: Elem, ftn: (Seq[Quad]) => T): Option[T] = {

    val page = XMLSource.fromXML(elem, context.language).head

    getIBProperties(page, ftn)
  }
  def getIBProperties[T](node: scala.xml.Node, ftn: (Seq[Quad]) => T): Option[T] = {

    val page = XMLSource.fromXML(getMediaElem(node), context.language).head

    getIBProperties(page, ftn)
  }

  private def getMediaElem(pageNode: scala.xml.Node) = {
    <mediawiki>
      { pageNode }
    </mediawiki>

  }
  def collectCategoryLinks(node: Node): List[InternalLinkNode] =
    {
      node match {
        case linkNode: InternalLinkNode if linkNode.destination.namespace == Namespace.Category => List(linkNode)
        case _ => node.children.flatMap(collectCategoryLinks)
      }
    }
  def isCategoryForArticle(linkNode: InternalLinkNode) = linkNode.destinationNodes match {
    case TextNode(text, _) :: Nil => !text.startsWith(":") // links starting wih ':' are actually only related, not the category of this article
    case _ => true
  }

  def extractRedirect(xmlElem: Elem, id: Boolean = false): Option[(String, String, String)] = {
    val page = XMLSource.fromXML(xmlElem, Language.English).head
    val namespaces = Set(Namespace.Main, Namespace.Template, Namespace.Category)
    parser(page) match {
      case Some(n) => {
        if (n.isRedirect && namespaces.contains(page.title.namespace)) {
          for (InternalLinkNode(destination, _, _, _) <- n.children) {

            if (!page.title.decoded.equalsIgnoreCase(destination.decoded))
              return Some((page.id + "", page.title.decoded, destination.decoded))
          }
        }
        return Some((page.id + "", page.title.decoded, ""))

      }
      case _ => return Some((page.id + "", page.title.decoded, ""))

    }

  }
  def extractCategories(xmlElem: Elem, id: Boolean = false): Option[(String, List[String])] = {
    val page = XMLSource.fromXML(xmlElem, Language.English).head

    parser(page) match {
      case Some(n) => {
        //   extractor.g
        val links = collectCategoryLinks(n).filter(isCategoryForArticle(_))
        val totalLinks = (for (
          each <- links if (each.retrieveText.getOrElse("").startsWith("Category:"))
        ) yield each.retrieveText.getOrElse("").split("Category:")(1).trim).toList

        Some(if (id) page.id + "" else page.revision + "", totalLinks)
      }
      case _ => None

    }
  }
  def extractDisAmbigLinks(xmlElem: Elem): Option[(String, List[String])] = {
    val page = XMLSource.fromXML(xmlElem, Language.English).head
    val replaceString = DisambiguationExtractorConfig.disambiguationTitlePartMap(context.language.wikiCode)
    parser(page) match {
      case Some(n) => {
        val allLinks = collectInternalLinks(n)

         if (!(page.title.namespace == Namespace.Main && n.isDisambiguation )) return None
        // use upper case to be case-insensitive. this also means we regard all titles as acronyms.
        val cleanPageTitle = page.title.decoded.replace(replaceString, "").toUpperCase(context.language.locale)

        // Extract only links that contain the page title or that spell out the acronym page title
        val disambigLinks = allLinks.filter { linkNode =>
          val cleanLink = linkNode.destination.decoded.toUpperCase(context.language.locale)
          cleanLink.contains(cleanPageTitle) || isAcronym(cleanPageTitle, cleanLink)
        }

        val totalLinks = (for (
          each <- disambigLinks if (each.retrieveText.getOrElse("").length() >0)
        ) yield each.retrieveText.getOrElse("").trim).toList

        Some(cleanPageTitle, totalLinks)

      }
      case _ => None

    }
  }
  private def isAcronym(acronym: String, destination: String): Boolean =
    {
      val destinationWithoutDash = destination.replace("-", " ")

      val destinationList =
        if (destinationWithoutDash.contains(" ")) destinationWithoutDash.split(" ")
        else destinationWithoutDash.split("")

      var matchCount = 0
      for (word <- destinationList) {
        if (word.toUpperCase(context.language.locale).startsWith(acronym(matchCount).toString)) matchCount += 1
        if (matchCount == acronym.length) return true
      }

      false
    }

  private def collectInternalLinks(node: Node): List[InternalLinkNode] = node match {
    case linkNode: InternalLinkNode => List(linkNode)
    case _ => node.children.flatMap(collectInternalLinks)
  }
  def extractTemplates(xmlElem: Elem, id: Boolean = false): Option[(String, List[String])] = {
    val page = XMLSource.fromXML(xmlElem, Language.English).head
    parser(page) match {
      case Some(node) => {
        val subjectUri = node.title.resourceIri
        if (node.title.namespace != Namespace.Main && !titleContainsCommonsMetadata(node.title)) return None
        val templates = new ArrayBuffer[String]()
        for {
          template <- collectTemplates(node)
          resolvedTitle = context.redirects.resolve(template.title).decoded.toLowerCase
          if !ignoreTemplates.contains(resolvedTitle)
          if !ignoreTemplatesRegex.exists(regex => regex.unapplySeq(resolvedTitle).isDefined)
        } {
          templates += resolvedTitle + ""

        }
        Some((page.id + "", templates.toList))
      }
      case _ => None

    }

  }

  def modifyMediawiki(x: String) = {
    "<mediawiki> " + x + " </mediawiki>"
  }

}

package com.recipegrace.dbmedia

import org.dbpedia.extraction.config.dataparser.DataParserConfig
import org.dbpedia.extraction.config.mappings.InfoboxExtractorConfig
import org.dbpedia.extraction.destinations.DBpediaDatasets
import org.dbpedia.extraction.destinations.Quad
import org.dbpedia.extraction.mappings.Redirects
import org.dbpedia.extraction.ontology.datatypes.Datatype
import org.dbpedia.extraction.ontology.io.OntologyReader
import org.dbpedia.extraction.sources.XMLSource
import org.dbpedia.extraction.util.Language
import org.dbpedia.extraction.util.RichString.wrapString
import org.dbpedia.extraction.util.WikiUtil
import org.dbpedia.extraction.wikiparser.Namespace
import org.dbpedia.extraction.wikiparser.Node
import org.dbpedia.extraction.wikiparser.NodeUtil
import org.dbpedia.extraction.wikiparser.PageNode
import org.dbpedia.extraction.wikiparser.PropertyNode
import org.dbpedia.extraction.wikiparser.TemplateNode
import org.dbpedia.extraction.wikiparser.WikiTitle
import scala.xml.XML
import scala.collection.mutable.ArrayBuffer
import org.dbpedia.extraction.wikiparser.impl.simple.SimpleWikiParser
import org.dbpedia.extraction.dataparser.StringParser
import org.dbpedia.extraction.ontology.datatypes.DimensionDatatype
import org.dbpedia.extraction.dataparser.UnitValueParser
import org.dbpedia.extraction.ontology.Ontology
import org.dbpedia.extraction.dataparser.IntegerParser
import org.dbpedia.extraction.dataparser.DoubleParser
import org.dbpedia.extraction.dataparser.DateTimeParser
import org.dbpedia.extraction.dataparser.ObjectParser
import org.dbpedia.extraction.dataparser.LinkParser
import org.dbpedia.extraction.util.UriUtils
import scala.util.Try

/*
 * 
 * Heavily influenced by the DBPedia InfoBoxparser but 3.9 version read the onthology file for every extract function
 * 
 */

object CBInfoBoxParser {
  private val wikiCode = Language.English.wikiCode

   val   context = new  {
    def ontology : Ontology={
    val in = getClass().getResourceAsStream("/ontology.xml");

    require(in!=null, "ontology.xml is not found in the classpath")
    val xmlElem = XML.load(in)

    val ontologySource = XMLSource.fromXML(xmlElem, Language.Mappings)
    new OntologyReader().read(ontologySource)
  }
    def language : Language = Language.English
    def redirects : Redirects = new Redirects(Map())
  } 
  private val minPropertyCount = InfoboxExtractorConfig.minPropertyCount
  private val ignoreTemplates = InfoboxExtractorConfig.ignoreTemplates
  private val ignoreProperties = InfoboxExtractorConfig.ignoreProperties
  private val ignoreTemplatesRegex = InfoboxExtractorConfig.ignoreTemplatesRegex
  private val minRatioOfExplicitPropertyKeys = InfoboxExtractorConfig.minRatioOfExplicitPropertyKeys
  private val SplitWordsRegex = InfoboxExtractorConfig.SplitWordsRegex
  private val splitPropertyNodeRegexInfobox = DataParserConfig.splitPropertyNodeRegexInfobox.get("en").get
  private val TrailingNumberRegex = InfoboxExtractorConfig.TrailingNumberRegex
  private val labelProperty = context.ontology.properties("rdfs:label")
  private val typeProperty = context.ontology.properties("rdf:type")
  private val propertyClass =  context.ontology.classes("rdf:Property")
  private val parser = new SimpleWikiParser()
    private val RankRegex = InfoboxExtractorConfig.RankRegex
      private val unitValueParsers =  context.ontology.datatypes.values
                                   .filter(_.isInstanceOf[DimensionDatatype])
                                   .map(dimension => new UnitValueParser(context, dimension, true))

    private val intParser = new IntegerParser(context, true, validRange = (i => i%1==0))

    private val doubleParser = new DoubleParser(context, true)

    private val dateTimeParsers = List("xsd:date", "xsd:gMonthYear", "xsd:gMonthDay", "xsd:gMonth" /*, "xsd:gYear", "xsd:gDay"*/)
                                  .map(datatype => new DateTimeParser(context, new Datatype(datatype), true))

                                  
    private val objectParser = new ObjectParser(context, true)

    private val linkParser = new LinkParser(true)

  val language = Language.English

  val commonsNamespacesContainingMetadata: Set[Namespace] = try {
    Set[Namespace](
      Namespace.Main,
      Namespace.File,
      Namespace.Category,
      Namespace.Template,
      Namespace.get(Language.Commons, "Creator").get,
      Namespace.get(Language.Commons, "Institution").get)
  } catch {
    case ex: java.util.NoSuchElementException =>
      throw new RuntimeException("Commons namespace not correctly set up: " +
        "make sure namespaces 'Creator' and 'Institution' are defined in " +
        "settings/commonswiki-configuration.xml")
  }

  /**
   * Check if this WikiTitle is (1) on the Commons, and (2) contains metadata.
   */

  private def titleContainsCommonsMetadata(title: WikiTitle): Boolean =
    (title.language == Language.Commons && commonsNamespacesContainingMetadata.contains(title.namespace))
  private def collectTemplates(node: Node): List[TemplateNode] =
    {
      node match {
        case templateNode: TemplateNode => List(templateNode)
        case _ => node.children.flatMap(collectTemplates)
      }
    }
  private def getPropertyUri(key: String): String =
    {
      // convert property key to camelCase
      var result = key.toLowerCase(Language.English.locale).trim
      result = result.toCamelCase(SplitWordsRegex, language.locale)

      // Rename Properties like LeaderName1, LeaderName2, ... to LeaderName
      result = TrailingNumberRegex.replaceFirstIn(result, "")

      result = WikiUtil.cleanSpace(result)

      language.propertyUri.append(result)
    }
  private def getPropertyLabel(key: String): String =
    {
      // convert property key to camelCase
      var result = key

      result = result.replace("_", " ")

      // Rename Properties like LeaderName1, LeaderName2, ... to LeaderName
      result = TrailingNumberRegex.replaceFirstIn(result, "")

      result
    }
 private def extractValue(node : PropertyNode) : List[(String, Datatype)] =
    {
        // TODO don't convert to SI units (what happens to {{convert|25|kg}} ?)
        extractUnitValue(node).foreach(result => return List(result))
        extractDates(node) match
        {
            case dates if !dates.isEmpty => return dates
            case _ => 
        }
       // extractSingleCoordinate(node).foreach(result =>  return List(result))
        extractNumber(node).foreach(result =>  return List(result))
        extractRankNumber(node).foreach(result => return List(result))
        extractLinks(node) match
        {
            case links if !links.isEmpty => return links
            case _ =>
        }
        StringParser.parse(node).map(value => (value, new Datatype("rdf:langString"))).toList
    }

    private def extractUnitValue(node : PropertyNode) : Option[(String, Datatype)] =
    {
        val unitValues =
        for (unitValueParser <- unitValueParsers;
             (value, unit) <- unitValueParser.parse(node) )
             yield (value, unit)

        if (unitValues.size > 1)
        {
            StringParser.parse(node).map(value => (value, new Datatype("rdf:langString")))
        }
        else if (unitValues.size == 1)
        {
            val (value, unit) = unitValues.head
            Some((value.toString, unit))
        }
        else
        {
            None
        }
    }

    private def extractNumber(node : PropertyNode) : Option[(String, Datatype)] =
    {
        intParser.parse(node).foreach(value => return Some((value.toString, new Datatype("xsd:integer"))))
        doubleParser.parse(node).foreach(value => return Some((value.toString, new Datatype("xsd:double"))))
        None
    }

    private def extractRankNumber(node : PropertyNode) : Option[(String, Datatype)] =
    {
        StringParser.parse(node) match
        {
            case Some(RankRegex(number)) => Some((number, new Datatype("xsd:integer")))
            case _ => None
        }
    }
    
 /*   private def extractSingleCoordinate(node : PropertyNode) : Option[(String, Datatype)] =
    {
        singleGeoCoordinateParser.parse(node).foreach(value => return Some((value.toDouble.toString, new Datatype("xsd:double"))))
        None
    }
*/
    private def extractDates(node : PropertyNode) : List[(String, Datatype)] =
    {
        for(date <- extractDate(node))
        {
            return List(date)
        }

        //Split the node. Note that even if some of these hyphens are looking similar, they represent different Unicode numbers.
        val splitNodes = NodeUtil.splitPropertyNode(node, "(—|–|-|&mdash;|&ndash;|,|;)")

        splitNodes.flatMap(extractDate(_)) match
        {
            case dates if dates.size == splitNodes.size => dates
            case _ => List.empty
        }
    }
    
    private def extractDate(node : PropertyNode) : Option[(String, Datatype)] =
    {
        for (dateTimeParser <- dateTimeParsers;
             date <- dateTimeParser.parse(node))
        {
            return Some((date.toString, date.datatype))
        }
        None
    }

    private def extractLinks(node : PropertyNode) : List[(String, Datatype)] =
    {
        val splitNodes = NodeUtil.splitPropertyNode(node, """\s*\W+\s*""")
       
        splitNodes.flatMap(splitNode => Try{objectParser.parse(splitNode)}.getOrElse(None)) match
        {
            // TODO: explain why we check links.size == splitNodes.size
            case links if links.size == splitNodes.size => return links.map(link => (link, null))
            case _ => List.empty
        }
        
        splitNodes.flatMap(splitNode => linkParser.parse(splitNode)) match
        {
            // TODO: explain why we check links.size == splitNodes.size
            case links if links.size == splitNodes.size => links.map(UriUtils.cleanLink).collect{case Some(link) => (link, null)}
            case _ => List.empty
        }
    }
  def extractIB(elem: scala.xml.Elem): Seq[Quad] = {

    
    val wikiPage = XMLSource.fromXML(elem, Language.Mappings).head
   

    parser(wikiPage) match {
      case Some(n) => {
        //   extractor.g
        extractIB(n)

      }
      case _ => Seq()

    }
  }
  def extractIB(node: scala.xml.Node): Seq[Quad] = {
    val pageNode = <mediawiki>
                     { node }
                   </mediawiki>
    extractIB(pageNode)
  }
    private def extractIBTTitle(node: PageNode): Option[List[String]] = {

    val subjectUri = node.title.resourceIri
    if (node.title.namespace != Namespace.Main && !titleContainsCommonsMetadata(node.title)) return None
    val templates = new ArrayBuffer[String]()
    for {
      template <- collectTemplates(node)
      resolvedTitle = context.redirects.resolve(template.title).decoded.toLowerCase
      if !ignoreTemplates.contains(resolvedTitle)
      if !ignoreTemplatesRegex.exists(regex => regex.unapplySeq(resolvedTitle).isDefined)
    } 
    { templates+=template.title+""
      
    }
   Some( templates.toList)


    
    
  }
  private def extractIB(node: PageNode): Seq[Quad] = {

    val subjectUri = node.title.resourceIri
    if (node.title.namespace != Namespace.Main && !titleContainsCommonsMetadata(node.title)) return Seq.empty
    val quads = new ArrayBuffer[Quad]()
    for {
      template <- collectTemplates(node)
      resolvedTitle = context.redirects.resolve(template.title).decoded.toLowerCase
      if !ignoreTemplates.contains(resolvedTitle)
      if !ignoreTemplatesRegex.exists(regex => regex.unapplySeq(resolvedTitle).isDefined)
    } {
     

      val propertyList = template.children.filterNot(property => ignoreProperties.get(wikiCode).getOrElse(ignoreProperties("en")).contains(property.key.toLowerCase))

      // check how many property keys are explicitly defined
      val countExplicitPropertyKeys = propertyList.count(property => !property.key.forall(_.isDigit))
      if ((countExplicitPropertyKeys >= minPropertyCount) && (countExplicitPropertyKeys.toDouble / propertyList.size) > minRatioOfExplicitPropertyKeys) {
        for (property <- propertyList; if (!property.key.forall(_.isDigit))) {
          // TODO clean HTML

          val cleanedPropertyNode = NodeUtil.removeParentheses(property)

          val splitPropertyNodes = NodeUtil.splitPropertyNode(cleanedPropertyNode, splitPropertyNodeRegexInfobox)
          for (splitNode <- splitPropertyNodes; (value, datatype) <- extractValue(splitNode)) {
            val propertyUri = getPropertyUri(property.key)
            try {
              quads += new Quad(language, DBpediaDatasets.InfoboxProperties, subjectUri, propertyUri, value, splitNode.sourceUri, datatype)
              val propertyLabel = getPropertyLabel(property.key)
              quads += new Quad(language, DBpediaDatasets.InfoboxPropertyDefinitions, propertyUri, typeProperty, propertyClass.uri, splitNode.sourceUri)
              quads += new Quad(language, DBpediaDatasets.InfoboxPropertyDefinitions, propertyUri, labelProperty, propertyLabel, splitNode.sourceUri, new Datatype("rdf:langString"))

            } catch {
              case ex: IllegalArgumentException => println(ex)
            }

          }

        }
      }

    }
    quads
  }
}

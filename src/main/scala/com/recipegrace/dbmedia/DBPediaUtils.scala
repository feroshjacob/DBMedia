package com.recipegrace.dbmedia


import org.dbpedia.extraction.wikiparser.WikiTitle
import org.dbpedia.extraction.wikiparser.TemplateNode
import org.dbpedia.extraction.util.Language
import org.dbpedia.extraction.wikiparser.Node
import org.dbpedia.extraction.wikiparser.Namespace
import org.dbpedia.extraction.config.mappings.InfoboxExtractorConfig


trait DBPediaUtils {
    val ignoreTemplates = InfoboxExtractorConfig.ignoreTemplates
       val ignoreTemplatesRegex = InfoboxExtractorConfig.ignoreTemplatesRegex  

   def titleContainsCommonsMetadata(title: WikiTitle): Boolean =
    (title.language == Language.Commons && commonsNamespacesContainingMetadata.contains(title.namespace))
   def collectTemplates(node: Node): List[TemplateNode] =
    {
      node match {
        case templateNode: TemplateNode => List(templateNode)
        case _ => node.children.flatMap(collectTemplates)
      }
    }
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
}

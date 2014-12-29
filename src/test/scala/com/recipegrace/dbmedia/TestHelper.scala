package com.recipegrace.dbmedia

import scala.xml.XML
import org.scalatest.FunSuite

import org.scalatest.Matchers

trait TestHelper extends FunSuite with Matchers {
  val wikiText1 = XML.loadString("""
      <mediawiki>
     <page> 
    <title>Actrius</title>
    <ns>0</ns>
    <id>332</id>
    <revision>
      <id>561898570</id>
      <parentid>539044257</parentid>
      <timestamp>2013-06-28T00:25:11Z</timestamp>
      <contributor>
        <username>ChrisGualtieri</username>
        <id>16333418</id>
      </contributor>
      <minor />
      <comment>General Fixes + DMY/MDY Tagging on Date O/I/A using [[Project:AWB|AWB]]</comment>
      <text xml:space="preserve">
      {{Redirect2|Oscars|The Oscar|the film|The Oscar (film)|other uses of the word &quot;Oscar&quot;|Oscar (disambiguation)}}
      {{Use dmy dates|date=June 2013}}
{{No footnotes|date=February 2012}}
{{Infobox film
| name           = Actrius
| image          = 
| caption        = 
| director       = [[Ventura Pons]]
| producer       = Ventura Pons
| Industry       = farming 
| country        = United States
| alt            = &lt;!-- see WP:ALT --&gt;
}}

'''''Actrius''''' ([[Catalan language|Catalan]]: ''Actresses'') is a 1996 film directed by [[Ventura Pons]]. In the film, there are no male actors and the four leading actresses dubbed themselves in the Castilian version.

==Synopsis==
In order to prepare the role of an important old actress, a theatre student interviews three actresses who were her pupils: an international diva (Glòria Marc, played by [[Núria Espert]]), a television star (Assumpta Roca, played by [[Rosa Maria Sardà]]) and a dubbing director (Maria Caminal, played by [[Anna Lizaran]]).

==External links==
*{{IMDb title|0115462|Actrius}}
*[http://www.venturapons.com/filmografia/actrices.html Ficha técnica]

[[Category:1996 films]]
[[Category:1990s drama films]]
[[Category:Spanish films]]
[[Category:Catalan-language films]]
[[Category:Films set in Barcelona]]
[[Category:Barcelona in fiction]]
[[Category:Films directed by Ventura Pons]]


{{1990s-drama-film-stub}}
{{Spain-film-stub}}</text>
      <sha1>9itmv7icc8y83mfyxyb5quyapt253hx</sha1>
      <model>wikitext</model>
      <format>text/x-wiki</format>
    </revision>
  </page>
      </mediawiki>
      """)
    
    val wikiText2 =  XML.loadString("""
      <mediawiki>
     <page>
    <title>Actrius</title>
    <ns>0</ns>
    <id>333</id>
    <revision> 
      <id>561898570</id>
      <parentid>539044257</parentid>
      <timestamp>2013-06-28T00:25:11Z</timestamp>
      <contributor>
        <username>ChrisGualtieri</username>
        <id>16333418</id>
      </contributor>
      <minor />
      <comment>General Fixes + DMY/MDY Tagging on Date O/I/A using [[Project:AWB|AWB]]</comment>
      <text xml:space="preserve">
</text>
      <sha1>9itmv7icc8y83mfyxyb5quyapt253hx</sha1>
      <model>wikitext</model>
      <format>text/x-wiki</format>
    </revision>
  </page>
      </mediawiki>""")
      
      val wikiText3 = XML.loadString("""
           <mediawiki>
            <page>
    <title>Action Film</title>
    <ns>0</ns>
    <id>325</id>
    <redirect title="Action films" />
    <revision>
      <id>160875351</id>
      <parentid>61398398</parentid>
      <timestamp>2007-09-28T08:27:52Z</timestamp>
      <contributor>
        <username>Closedmouth</username>
        <id>372693</id>
      </contributor>
      <comment>R from other capitalisation</comment>
      <text xml:space="preserve">#REDIRECT [[Action films]]{{R from other capitalisation}}</text>
      <sha1>5k8i2fnf0ikroxdc157isw1vs0bp8ed</sha1>
      <model>wikitext</model>
      <format>text/x-wiki</format>
    </revision>
  </page>
    </mediawiki>   """)
        val wikiText4 = XML.loadString("""
           <mediawiki>
            <page>
    <title>Action Film</title>
    <ns>0</ns>
    <id>326</id>
    <redirect title="Action films" />
    <revision>
      <id>160875351</id>
      <parentid>61398398</parentid>
      <timestamp>2007-09-28T08:27:52Z</timestamp>
      <contributor>
        <username>Closedmouth</username>
        <id>372693</id>
      </contributor>
      <comment>R from other capitalisation</comment>
      <text xml:space="preserve">#REDIRECT [[Action film]]{{R from other capitalisation}}</text>
      <sha1>5k8i2fnf0ikroxdc157isw1vs0bp8ed</sha1>
      <model>wikitext</model>
      <format>text/x-wiki</format>
    </revision>
  </page>
    </mediawiki>   """)
      val wikiText5 = XML.loadString("""
      <mediawiki>
     <page>
    <title>UAB (disambiguation)</title>
    <ns>0</ns>
    <id>332</id>
    <revision>
      <id>561898570</id>
      <parentid>539044257</parentid>
      <timestamp>2013-06-28T00:25:11Z</timestamp>
      <contributor>
        <username>ChrisGualtieri</username>
        <id>16333418</id>
      </contributor>
      <minor />
      <comment>General Fixes + DMY/MDY Tagging on Date O/I/A using [[Project:AWB|AWB]]</comment>
      <text xml:space="preserve">
 
    '''UAB''' may stand for:

*[[Lascivious]] or unacceptable behavior
*Until advised by
*User address book
*[[Uždaroji akcinė bendrovė]], a type of limited liability company in Lithuania

;Companies and organizations:
*[[United Airways (Bangladesh)]]
*[[Autonomous University of Barcelona]]
*[[University of Alabama at Birmingham]]
** [[UAB Blazers]], the athletic program of the above school
*[[University of Alberta]]
*[[Unemployment Assistance Board]]
*[[Underwater Archaeology Branch, Naval History  &amp; Heritage Command]]
*[[Unix AppleTalk Bridge]]
*[[University Activities Board]] ([[Clarion University of Pennsylvania]])
*[[Union des Automobilistes Bulgares]], a member of the [[FIA]]

{{disambig}}
 </text>
 <sha1>9itmv7icc8y83mfyxyb5quyapt253hx</sha1>
      <model>wikitext</model>
      <format>text/x-wiki</format>
    </revision>
  </page>
      </mediawiki>
      """)
            val wikiText6 = XML.loadString("""
      <mediawiki>
     <page>
    <title>Columbia University (disambiguation)</title>
    <ns>0</ns>
    <id>332</id>
    <revision>
      <id>561898570</id>
      <parentid>539044257</parentid>
      <timestamp>2013-06-28T00:25:11Z</timestamp>
      <contributor>
        <username>ChrisGualtieri</username>
        <id>16333418</id>
      </contributor>
      <minor />
      <comment>General Fixes + DMY/MDY Tagging on Date O/I/A using [[Project:AWB|AWB]]</comment>
      <text xml:space="preserve">
    
      '''[[Columbia University]]''' is the colloquial name of Columbia University in the City of New York. This may also refer to:

*The [[University of Portland]], formerly known as Columbia University

==See also==
* [[Columbia College, Columbia University]]
* [[Columbia College Chicago]], colloquially known in Chicago as "Columbia"
* [[Columbia College (Missouri)]], a private college based in [[Columbia, MO]]
* [[Columbia College (South Carolina)]], a private, women's liberal arts college located in [[Columbia, SC]]
* [[Columbia College (California)]], a community college located in [[Sonora, CA]]
* [[Columbia College Hollywood]], a private film school in [[Hollywood, CA]]
* [[National University of Colombia]], in Colombia, South America
* [[South Colombian University]], in Colombia, South America
* [[Columbia College (disambiguation)]]

{{disambig}}

 </text>
 <sha1>9itmv7icc8y83mfyxyb5quyapt253hx</sha1>
      <model>wikitext</model>
      <format>text/x-wiki</format>
    </revision>
  </page>
      </mediawiki>
      """)
      
}

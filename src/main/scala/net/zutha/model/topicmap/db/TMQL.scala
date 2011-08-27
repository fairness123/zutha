package net.zutha.model.topicmap.db

import scala.collection.JavaConversions._
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory
import de.topicmapslab.tmql4j.path.query.TMQLQuery
import de.topicmapslab.tmql4j.components.processor.results.model.{IResultSet}
import org.tmapi.core.TopicMap
import de.topicmapslab.tmql4j.components.processor.results.xml.XMLValue
import xml._

trait TMQL {
  def tm: TopicMap;

  private lazy val runtime = TMQLRuntimeFactory.newFactory().newRuntime("tmql-2007")

  //register Zutha prefixes
  private val prefixHandler = runtime.getLanguageContext().getPrefixHandler()
  prefixHandler.registerPrefix("zid", "http://zutha.net/item/")
  prefixHandler.registerPrefix("zsi", "http://psi.zutha.net/")

  //TODO cache prepared statements
  def prepareStatement(qstr: String) = {
    val query = new TMQLQuery(tm, qstr)
    runtime.preparedStatement(query)
  }

  def runQuery(qstr : String): IResultSet[_] = {
    val query = runtime.run(tm,qstr)
    val res = query.getResults
    return res
  }


  def XmlQueryResultsToNodeSeq(rs : IResultSet[_]): NodeSeq = {
    rs.getResultType match{
      case "XML" =>
        val allxml = rs.getResults.foldRight(NodeSeq.Empty){(r, acc) =>
          val flatXml = r.asInstanceOf[XMLValue].first().toString
          val ns = XML.loadString(flatXml)
          val newacc = ns ++: acc
          newacc
        }
        allxml
      case _ => throw new IllegalArgumentException
    }
  }

  /**
   * @param qstr a TMQL query that will return XML results
   * @return the results as a NodeSeq
   */
  def runXmlQuery(qstr: String): NodeSeq = {
    val rs = runQuery(qstr)
    XmlQueryResultsToNodeSeq(rs)
  }

  def queryResultsToString(rs : IResultSet[_]): String = {
    rs.getResultType match{
      case "XML" =>
        val pp = new PrettyPrinter(80,3)
        try{
          val allxml = XmlQueryResultsToNodeSeq(rs)
          val prettyXml = pp.formatNodes(allxml)
          prettyXml
        } catch {
          case _ => rs.toString
        }

      case _ => rs.toString
    }
  }

  def getPrefixes = runtime.getLanguageContext().getPrefixHandler().getPrefixMap
}

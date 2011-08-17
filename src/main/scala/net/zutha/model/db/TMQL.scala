package net.zutha.model.db

import scala.collection.JavaConversions._
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory
import de.topicmapslab.tmql4j.path.query.TMQLQuery
import de.topicmapslab.tmql4j.components.processor.results.model.{IResult, IResultSet}
import org.tmapi.core.TopicMap
import de.topicmapslab.tmql4j.components.processor.results.xml.XMLValue
import xml._

trait TMQL {
  val tm: TopicMap

  lazy val runtime = TMQLRuntimeFactory.newFactory().newRuntime("tmql-2007")

  //register Zutha prefixes
  val prefixHandler = runtime.getLanguageContext().getPrefixHandler()
  prefixHandler.registerPrefix("zid", "http://zutha.net/item/")
  prefixHandler.registerPrefix("zsi", "http://psi.zutha.net/")

  def runQuery(qstr : String): IResultSet[_] = {
    val query = new TMQLQuery(tm, qstr)
    val statement = runtime.preparedStatement(query)
    statement.run()
    val res = statement.getResults
    return res
  }


  def queryResultsToNodeSeq(rs : IResultSet[_]): NodeSeq = {
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
    queryResultsToNodeSeq(rs)
  }

  def queryResultsToString(rs : IResultSet[_]): String = {
    rs.getResultType match{
      case "XML" =>
        val pp = new PrettyPrinter(80,3)
        try{
          val allxml = queryResultsToNodeSeq(rs)
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

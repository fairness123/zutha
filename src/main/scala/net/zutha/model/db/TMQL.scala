package net.zutha.model.db

import scala.collection.JavaConversions._
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory
import de.topicmapslab.tmql4j.path.query.TMQLQuery
import de.topicmapslab.tmql4j.components.processor.results.model.{IResult, IResultSet}
import org.tmapi.core.TopicMap


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

  def resultsAsTable(res : IResultSet[_]): Seq[Seq[String]] = {
    res.getResults.map{r =>
      r.asInstanceOf[IResult].getResults.map{obj =>
        obj.toString
      }
    }
  }

  def getPrefixes = runtime.getLanguageContext().getPrefixHandler().getPrefixMap
}

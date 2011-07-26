package net.zutha
package model

import scala.collection.JavaConversions._
import org.tmapi.core._
import de.topicmapslab.majortom.store.TopicMapStoreProperty
import de.topicmapslab.majortom.util.FeatureStrings
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet
import de.topicmapslab.tmql4j.components.processor.results.model.IResult
import de.topicmapslab.tmql4j.path.query.TMQLQuery

object QueryEngine {
  private lazy val sys = majortomPostgresSystem
  private lazy val tm = zuthaTopicMap
  private lazy val runtime = defaultTMQLRuntime
  
  def runQuery(qstr : String): String = {
    val query = new TMQLQuery(tm, qstr)
    val statement = runtime.preparedStatement(query)
    statement.run()
    val res = statement.getResults
    return resultsAsString(res)
  }
  
  private def resultsAsString(res : IResultSet[_]) = res.toString
  
  private def resultsAsTable(res : IResultSet[_]): Seq[Seq[String]] = {
    res.getResults.map{r =>
      r.asInstanceOf[IResult].getResults.map{obj =>
        obj.toString
      }
    }
  }
  
  def getPrefixes = runtime.getLanguageContext().getPrefixHandler().getPrefixMap
  
  def printTMLocators = {
    for(val loc <- sys.getLocators) {
      println (loc.getReference)
    }
  }
  
  def zuthaTopicMap = {
    val uri = "http://zutha.net"
    val tm = sys.getTopicMap(uri)
    if(tm==null) 
      sys.createTopicMap(uri)
    else
      tm
  }
  
  def defaultTMQLRuntime = {
    val runtime = TMQLRuntimeFactory.newFactory().newRuntime("tmql-2007")
    
    //register Zutha prefixes
    val prefixHandler = runtime.getLanguageContext().getPrefixHandler()
    prefixHandler.registerPrefix("zid", "http://zutha.net/item/")
    prefixHandler.registerPrefix("z", "http://psi.zutha.net/")
    runtime
  }
  
  def majortomPostgresSystem = {
    val factory = TopicMapSystemFactory.newInstance();
    factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,"de.topicmapslab.majortom.database.store.JdbcTopicMapStore")
    factory.setProperty("de.topicmapslab.majortom.jdbc.host", "localhost")
    factory.setProperty("de.topicmapslab.majortom.jdbc.database", "majortom")
    factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres")
    factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres")
    factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL")
    factory.setFeature(FeatureStrings.SUPPORT_HISTORY, true);
    
    factory.newTopicMapSystem()
  }
}
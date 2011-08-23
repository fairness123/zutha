package net.zutha.snippet

import scala.collection.JavaConversions._
import net.liftweb._
import http._
import util._
import Helpers._
import net.zutha.model.topicmap.TopicMapDB

class ZuthaConsole extends StatefulSnippet {
  private var queryStr = ""
  private var queryRes = ""
    
  def dispatch = {
    case "query" => runQuery
    case "prefixes" => prefixes
    case "buttons" => buttons
  }
    
  def prefixes = ".prefix_entry *" #> TopicMapDB.getPrefixes.map{pre =>
      ".prefix" #> pre._1 &
      ".uri" #> pre._2
  }

  def runQuery =
    "textarea" #> SHtml.textarea(queryStr, (queryStr = _ )) &
    "#query_output *" #> queryRes &
    "type=submit" #> SHtml.onSubmitUnit(processQuery)
 
  def processQuery() = {
      val res = TopicMapDB.runQuery(queryStr)
      queryRes = TopicMapDB.queryResultsToString(res)
    }

  def buttons =
    ".reset" #> SHtml.button("Reset",TopicMapDB.resetDBtoSchema) &
    ".getPath" #> SHtml.button("getPath",{() =>
      val rootDir = new java.io.File(".")
      val rootStr = rootDir.getAbsolutePath
      S.notice(rootStr)
    })

}

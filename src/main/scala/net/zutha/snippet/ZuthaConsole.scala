package net.zutha.snippet

import scala.collection.JavaConversions._
import net.liftweb._
import http._
import common._
import util._
import Helpers._
import scala.xml.NodeSeq
import net.zutha.model.QueryEngine

object ZuthaConsole extends StatefulSnippet {
  private var queryStr = ""
  private var queryRes = ""
    
  def dispatch = {
    case "query" => runQuery
    case "prefixes" => prefixes
  }
    
  def prefixes = ".prefix_entry *" #> QueryEngine.getPrefixes.map{pre => 
      ".prefix" #> pre._1 &
      ".uri" #> pre._2
  }
    

    
  def runQuery =
    "textarea" #> SHtml.textarea(queryStr, queryStr = _) &
    "#query_output *" #> queryRes &
    "type=submit" #> SHtml.onSubmitUnit(processQuery)
 
  def processQuery() = {
      queryRes = QueryEngine.runQuery(queryStr)
    }
}
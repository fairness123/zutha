package net.zutha.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import js.JsCmds._
import js.jquery._
import _root_.scala.xml.{NodeSeq, Text}
import scala.collection.mutable.LinkedHashMap

object CreateItem {
  //container class to store Subject Indicators
  case class SI(guid: String, uri: String)
  
  val SIs = ValueCell[List[SI]](List(newSI))
  
  val createdItemXML = SIs.lift(_.foldLeft(""){(str,si) =>
    str ++ (<subjInd>{si.uri}</subjInd>).toString + '\n'
  })
  
  //val SIs = LinkedHashMap[String,String]()
  
  def render = {
    S.notice("form being rendered. XML: " + createdItemXML)
    "#subject_indicators *" #> SIs.flatMap(renderSI) &
    "#addSI" #> addSI _ &
    "type=submit" #> SHtml.submit("Submit", executeCreateItem) &
    "#item_xml *" #> createdItemXML.get
  }
  
  def executeCreateItem() = {
    S.notice("form submitted. SIs: " + SIs.toString())

    S.notice("generated item: " + createdItemXML.get)
  }
  
  def addSI(ns: NodeSeq): NodeSeq = {
    SHtml.ajaxButton(ns, () => {
      S.notice("add pressed")
      val theSI = appendSI
      JqJsCmds.AppendHtml("subject_indicators", renderSI(theSI))
    })
  }
  
  private def renderSI(theSI: SI): NodeSeq = {
    <div id={theSI.guid}>{
      SHtml.text(theSI.uri,
                     s => {
                       S.notice("processing SI: " + s)
                       mutateSI(theSI.guid) {
                         si => SI(si.guid, s)
                       }
                       Noop
                     })
    }
    </div>
  }
  
  private def newSI = SI(nextFuncName,"")
  
  private def appendSI: SI = {
    val retn = newSI
    SIs.atomicUpdate(_ :+ retn)
    retn
  }
  
  private def mutateSI(guid: String)(f: SI => SI) {
    SIs.atomicUpdate(_.map(si => if (si.guid == guid) f(si) else si))
    
    S.notice("new SIs: " + SIs.toString)
  }
  
  def removeSI(guid: String) {
    SIs.atomicUpdate(_.filterNot(_.guid == guid))
  }
}
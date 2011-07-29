package net.zutha.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import js.JsCmds._
import js.jquery._
import _root_.scala.xml.{NodeSeq}


object CreateItem {
  //container class to store Subject Indicators
  case class SI_Line(guid: String, uri: String)
  
  val SIs = ValueCell[List[SI_Line]](List(newSI))
  
  val createdItemXML = SIs.lift(_.foldLeft(""){(str,si) =>
    str + (<subjInd>{si.uri}</subjInd>).toString + "\n"
  })
  
  //val SIs = LinkedHashMap[String,String]()
  
  def render = {
    "#SI_block *" #> render_SIs _ &
    "type=submit" #> SHtml.submit("Submit", executeCreateItem) &
    "#item_xml *" #> createdItemXML.get
  }



  def render_SIs(ns: NodeSeq): NodeSeq = {
	//use the html in the element with class=SI_line as a basis for rendering each SI
	val si_line_ns: NodeSeq = (".SI_line ^^" #> "")(ns)
	//render a SI line for each SI_line in SIs
	(".SI_line" #> SIs.get.map(render_SI(_)(si_line_ns)) &
	//si_line_ns will be used for the JsCmd that generates new SI_line blocks
	"#addSI *" #> render_addSI(si_line_ns) _ )(ns)
  }
  
  def render_SI(theSI: SI_Line): NodeSeq => NodeSeq = {
	".SI_line [id]" #> theSI.guid &
    "@SI" #> SHtml.text(theSI.uri,
				 s => { mutateSI(theSI.guid) {
					 si => SI_Line(si.guid, s) }
				 }) &
	"@remove" #> SHtml.ajaxButton("Remove", () => {
		removeSI(theSI.guid)
		JqJsCmds.JqSetHtml(theSI.guid,Nil)
	})
  }
  
  def render_addSI(si_line_ns: NodeSeq)(add_ns: NodeSeq): NodeSeq = {
    SHtml.ajaxButton(add_ns, () => {
      val theSI = appendSI
      JqJsCmds.AppendHtml("SIs", render_SI(theSI)(si_line_ns))
    })
  }
  
  def executeCreateItem() = {
    S.notice("form submitted.\nSIs: " + SIs.get.toString())
  }
  

  
  private def newSI = SI_Line(nextFuncName,"")
  
  private def appendSI: SI_Line = {
    S.notice("adding line 2")
    val retn = newSI
    SIs.atomicUpdate(_ :+ retn)
    retn
  }

  private def mutateSI(guid: String)(f: SI_Line => SI_Line) {
    SIs.atomicUpdate(_.map(si => if (si.guid == guid) f(si) else si))
  }
  
  def removeSI(guid: String) {
    SIs.atomicUpdate(_.filterNot(_.guid == guid))
  }
}

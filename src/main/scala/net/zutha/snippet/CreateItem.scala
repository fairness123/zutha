package net.zutha.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import js.jquery._
import collection.mutable.ListBuffer
import xml.{PrettyPrinter, NodeSeq}


object CreateItem {
  abstract class PropertySet[T <: Property](html_id: String)(implicit m: Manifest[T]){
    val props = new ListBuffer[T]

    def newProp: T = {
      val prop = m.erasure.newInstance().asInstanceOf[T]
      prop.remove = () => removeProp(prop)
      prop
    }

    private def appendProp: T = {
      val prop = newProp
      props += prop
      prop
    }

    def removeProp(prop: T) {
      props -= prop
    }

    def render_props(ns: NodeSeq): NodeSeq = {
      //use the html in the element with class=property as a basis for rendering each property
      val prop_ns: NodeSeq = (".property ^^" #> "")(ns)
      def sel = ".property" #> props.map(_.render(prop_ns)) &
      //prop_ns will be used for the JsCmd that generates new property blocks
      ".addProp *" #> (add_ns => renderAddButton(prop_ns)(add_ns))
      sel(ns)
    }

    def renderAddButton(prop_ns: NodeSeq)(add_ns: NodeSeq): NodeSeq = {
      SHtml.ajaxButton(add_ns, () => {
        val theProp = appendProp
        JqJsCmds.AppendHtml(html_id, theProp.render(prop_ns))
      })
    }
  }

  abstract class Property {
    var remove = () => {}

    def render_fields: CssSel

    def render: NodeSeq => NodeSeq = {
      val guid: String = nextFuncName
      ".property [id]" #> guid &
      "@remove" #> SHtml.ajaxButton("Remove", () => {
        remove()
        JqJsCmds.JqSetHtml(guid,Nil)
      }) &
      render_fields
    }
  }


  object Names extends PropertySet[NameProp]("names")

  class NameProp() extends Property {
    var nameType = "Name"
    var name = ""
    var scope = ""

    override def render_fields = {
      "@nameType" #> SHtml.ajaxText(nameType,s => nameType = s) &
      "@name" #> SHtml.ajaxText(name,s => name = s) &
      "@scope" #> SHtml.ajaxText(scope,s => scope = s)
    }
  }

  object SIs extends PropertySet[SubjectIndicatorProp]("SIs")

  //container class to store Subject Indicators
  class SubjectIndicatorProp() extends Property {
    var uri = ""

    override def render_fields = {
      "@uri" #> SHtml.ajaxText(uri,s => uri = s)
    }
  }

  object Types extends PropertySet[TypeProp]("types")

  class TypeProp() extends Property {
    var id = ""
    var name = ""

    override def render_fields = {
      "@name" #> SHtml.ajaxText(name,s => name = s) &
      "@id" #> SHtml.hidden(s => id = s, id)
    }
  }

  

  def render = {
    "#SI_block *" #> SIs.render_props _ &
    "#type_block *" #> Types.render_props _ &
    "#name_block *" #> Names.render_props _ &
    "type=submit" #> SHtml.submit("Submit", () => executeCreateItem()) &
    "#item_xml *" #> createdItemXML
  }

  def executeCreateItem() {
    S.notice("form submitted...")
  }

  def createdItemXML: String = {
    val ns = <item>
      <Names>{
        Names.props.map{name =>
        <name type={name.nameType} scope={name.scope}>{name.name}</name>}
      }</Names>
      <Types>{
        Types.props.map{t =>
        <type id={t.id}>{t.name}</type>}
      }</Types>
      <SubjectIndicators>{
        SIs.props.map{si =>
        <subjInd>{si.uri}</subjInd>}
      }</SubjectIndicators>
    </item>
    val pp = new PrettyPrinter(80,3)
    val str = pp.formatNodes(ns)
    str
  }


}

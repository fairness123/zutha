package net.zutha.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import xml.{PrettyPrinter, NodeSeq}
import net.zutha.lib._

class CreateItem extends StatefulSnippet {

  val nameProps = new DynamicNameProps()

  val SubjectIndicatorProps = new DynamicSubjectIndicatorProps()

  val typeProps = new DynamicTypeProps()

  def dispatch = {case "render" => render}

  def render = {
    "#SI_block *" #> SubjectIndicatorProps.renderElements _ &
    "#type_block *" #> typeProps.renderElements _ &
    "#name_block *" #> nameProps.renderElements _ &
    "type=submit" #> SHtml.submit("Submit", () => executeCreateItem()) &
    "#item_xml *" #> createdItemXML
  }

  def executeCreateItem() {
    S.notice("form submitted...")
  }

  def createdItemXML: String = {
    val ns = <item>
      <Names>{
        nameProps.props.map{name =>
        <name type={name.nameType} scope={name.scope}>{name.name}</name>}
      }</Names>
      <Types>{
        typeProps.props.map{t =>
        <type id={t.id}>{t.name}</type>}
      }</Types>
      <SubjectIndicators>{
        SubjectIndicatorProps.props.map{si =>
        <subjInd>{si.uri}</subjInd>}
      }</SubjectIndicators>
    </item>
    val pp = new PrettyPrinter(80,3)
    val str = pp.formatNodes(ns)
    str
  }


}

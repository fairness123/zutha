package net.zutha.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import xml.{PrettyPrinter}
import net.zutha.model.properties.{SubjectIndicatorProps, TypeProps, NameProps}
import net.zutha.model.ProposedItem
import net.zutha.model.db.DB

class CreateItem extends StatefulSnippet {

  val nameProps = new NameProps()

  val subjectIndicatorProps = new SubjectIndicatorProps()

  val typeProps = new TypeProps()

  def dispatch = {case "render" => render}

  def render = {
    "#SI_block *" #> subjectIndicatorProps.renderElements _ &
    "#type_block *" #> typeProps.renderElements _ &
    "#name_block *" #> nameProps.renderElements _ &
    "type=submit" #> SHtml.submit("Submit", () => executeCreateItem()) &
    "#item_xml *" #> createdItemXML
  }

  def executeCreateItem() {
    S.notice("form submitted...")
    DB.get.createItem(ProposedItem(nameProps,subjectIndicatorProps, typeProps))
  }

  def createdItemXML: String = {
    val ns = <item>
      <Names>{
        nameProps.props.map{name =>
        <name type={name.typeZSI} >{name.value}</name>}
      }</Names>
      <Types>{
        typeProps.props.map{t =>
        <type>{t.typeZSI}</type>}
      }</Types>
      <SubjectIndicators>{
        subjectIndicatorProps.props.map{si =>
        <subjInd>{si.uri}</subjInd>}
      }</SubjectIndicators>
    </item>
    val pp = new PrettyPrinter(80,3)
    val str = pp.formatNodes(ns)
    str
  }


}

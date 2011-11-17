package net.zutha.snippet

import net.liftweb._
import common.{Logger, Full, Box, Empty}
import http._
import js.JsCmd
import js.JsCmds._
import util._
import Helpers._
import net.zutha.model.db.DB.db
import net.zutha.lib.uri.RoleInfo
import net.zutha.model.constructs.{ZTrait, Zid, ZAssociationFieldType}
import widgets.autocomplete._
import xml.{Text, NodeSeq}
import net.zutha.model.builder.{AssociationFieldSetBuilder, FieldSetBuilder, ItemBuilder}
import net.zutha.lib.widgets.{AssocTableEdit, SimpleFieldSetEdit}


class CreateItemForm(roleInfo: RoleInfo) extends Logger{
  private val item = roleInfo.item
  private val assocType = roleInfo.assocType
  private val role = roleInfo.role
  private val otherRole = roleInfo.otherRole

  private val requiredAssocFieldType = ZAssociationFieldType(otherRole,assocType)
  private val itemBuilderCell = ValueCell[ItemBuilder](
    new ItemBuilder(requiredAssocFieldType,(role,item)))

  // name autocomplete
  private def candidateItems(str: String,limit: Int):Seq[String] = {
    roleInfo.otherAssocFieldType.allowedPlayers.map(_.name).toSeq.sorted.take(limit)
  }
  private def setExistingItem(itemName: String){
    //TODO if itemName identifies a valid item, reconfigure create-item-form as an edit-form for this item
  }

  //item name
  private def setName(name: String) = {
    //TODO set name property in itemBuilder
    Noop
  }

  //itemType helpers
  private def selectedItemTypeZid = itemBuilderCell.is.selectedItemType.zid
  private val itemTypeOptions = itemBuilderCell.is.allowedItemTypes.map(it => (it.zid,it.name)).toSeq.sortBy(_._2)
  private def setItemType(zid:String): JsCmd = {
    try{
      val resolvedItemType = db.getItemByZid(Zid(zid)).get.toItemType
      itemBuilderCell.atomicUpdate{v =>
        v.selectedItemType = resolvedItemType
        v
      }
      Noop
    }
    catch {case _ => S.error("invalid ItemType selected")}
  }

  //trait helpers
  private val traitSelectState = itemBuilderCell.lift(ib => {
    (ib.selectedTrait.map(_.zid),ib.allowedTraits)
  })
  private def renderTraitsSelect( tSelState: (Box[String],Set[ZTrait]), ns: NodeSeq ) = {
    //val selectedTraitZid = ib.selectedTrait.map(_.zid)
    //val allowedTraits = ib.allowedTraits
    val (selectedTraitZid,allowedTraits) = tSelState
    if(allowedTraits.isEmpty) NodeSeq.Empty else{
      val traitOptions = allowedTraits.map(t => (t.zid,t.name)).toSeq.sortBy(_._2)
      val sel = "#select-trait" #> SHtml.ajaxSelect(traitOptions,selectedTraitZid,setTrait)
      sel(ns)
    }
  }
  private def setTrait( zid: String ): JsCmd = {
    try{
      val resolvedTrait = db.getItemByZid(Zid(zid)).get.toTrait
      itemBuilderCell.atomicUpdate{v =>
        v.selectedTrait = resolvedTrait
        v
      }
      Noop
    }
    catch {case _ => S.notice("error","invalid ItemType selected")}
  }

  //fields
  val fieldStateCell = itemBuilderCell.lift( ib => ib.fieldSets )

  def renderFields( fields: Set[FieldSetBuilder], ns: NodeSeq ) = {
    val complexFields = fields.collect{
      case afb:AssociationFieldSetBuilder if{
        val af = afb.fieldType
        af.otherRoles.size > 1 || af.propertyTypes.size > 0
      } => afb
    }
    val simpleFields = fields -- complexFields

    val sel =
      ".simple-field" #> simpleFields.toSeq.map{SimpleFieldSetEdit(_)} &
      ".complex-field" #> complexFields.toSeq.map{afsb =>
        ".field-type *" #> afsb.associationType.nameF(role) &
        ".association-table" #> AssocTableEdit(afsb)
      }

    sel(ns)
  }

  def render = {
    "#create-item-name" #> SHtml.ajaxText("",setName) &
    "#select-item-type" #> SHtml.ajaxSelect(itemTypeOptions,Full(selectedItemTypeZid),v => setItemType(v)) &
    "#trait" #> WiringUI.toNode(traitSelectState){renderTraitsSelect} &
    ".fields" #> WiringUI.toNode(fieldStateCell){renderFields}
  }
}

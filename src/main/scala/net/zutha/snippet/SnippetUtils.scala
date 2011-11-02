package net.zutha.snippet

import net.liftweb.util.Helpers._
import net.zutha.model.constructs.{ZAssociationField, ZAssociationFieldSet, ZItem}
import net.zutha.lib.uri.{RoleLoc, AssocLoc, ItemLoc}
import xml.{Text, NodeSeq}

object SnippetUtils {
  private[snippet] def makeElemList(elems: Seq[NodeSeq]): NodeSeq => NodeSeq = {
    val intermediate = if(elems.isEmpty) elems else elems.dropRight(1)
    ".intermediate *" #> intermediate.map{elem =>
      ".listval" #> elem} &
    ".last *" #> elems.lastOption.map{elem =>
      ".listval" #> elem}
  }
  private[snippet] def makeItemLinkList(items: Seq[ZItem]) = {
    val elems = items.map(makeItemLink)
    makeElemList(elems)
  }
  private[snippet] def makeItemLink(item: ZItem) = <a href={ItemLoc.makeUri(item)}>{item.name}</a>

  def itemSummary(item: ZItem, mainItem: Boolean = true): NodeSeq => NodeSeq = {
    ".zid *" #> item.zid &
    //Item Name
    ".name_selection" #> {ns =>
      val name_ns = if(mainItem) ("#main_item_name ^^" #> "")(ns)
        else (".subitem_name ^^" #> "")(ns)
      ("a *" #> item.name &
        "a [href]" #> ItemLoc.makeUri(item)
      )(name_ns)
    } &
    //Types
    ".item_type" #> makeItemLink(item.getType) &
    ".traits" #> makeItemLinkList(Seq(item.getType)) //TODO: get Item Traits
  }

  /** Render a table to display all association Fields in assocFieldSet*/
  def makeAssocSetTable(item: ZItem, assocFieldSet: ZAssociationFieldSet) = {
    val role = assocFieldSet.role
    val assocType = assocFieldSet.associationType
    val otherRoles = assocFieldSet.otherRoles.toSeq.sortBy(_.name)
    val propTypes = assocFieldSet.propertyTypes.toSeq.sortBy(_.name)

    def makeRow(assocField: ZAssociationField) = {
      ".role-players *" #> otherRoles.map{role =>
          val rolePlayers = assocField.companionAssociationFields.filter(_.role == role)
            .map(_.parent).toSeq.sortBy(_.zid) //TODO sort by worth
          SnippetUtils.makeItemLinkList(rolePlayers)
      } &
      ".prop-values *" #> propTypes.map{propType =>
          val propVals: Seq[NodeSeq] = assocField.association.getProperties(propType).toSeq.map(pv => Text(pv.valueString))
          SnippetUtils.makeElemList(propVals)
      }
    }

    ".role *" #> otherRoles.map{r =>
        "a *" #> r.name &
        "a [href]" #> RoleLoc.makeUri(item,role,assocType,r)
      } &
    ".prop-type *" #> propTypes.map{p => Text(p.nameF(role)):NodeSeq} &
    ".association-row *" #> assocFieldSet.associationFields.toSeq.map(makeRow(_)) //TODO sort by name of first member
  }
}

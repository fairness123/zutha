package net.zutha
package snippet

import _root_.scala.xml.{NodeSeq,Text}
import _root_.net.liftweb.util.Helpers
import Helpers._
import model.db.DB
import net.liftweb.common._
import Box._
import net.liftweb.http.{SHtml, SnippetExecutionException, S}
import model.constructs._

class CurrentItem {

  private lazy val item: Item = (for {
      zid <- S.param("zid") ?~ "no zid param was given"
      repairedZID <- Zid.repair(zid) ?~ "an invalid zid was provided"
      item <- DB.db.getItem(Zid(repairedZID)) ?~ "no item with the specifed zid can be found"
    } yield (item)) match{
      case Full(item) => item
      case Failure(msg, _, _) => throw new SnippetExecutionException(msg)
      case _ => throw new SnippetExecutionException("something went wrong looking up item by zid")
    }

  def pageTitle(content: NodeSeq): NodeSeq = {
    val name = S.param("name") openOr "<no name>"
    val viewStr = S.attr("view") match {
      case Full(view) => " - " + view
      case _ => ""
    }
    Text("Zutha.net - "+ name +viewStr)
  }

  def summary: NodeSeq => NodeSeq = {
    //ZID
    ".zid *" #> item.zid &
    //Item Name
    "@name_selection" #> {ns =>
      val name_ns = ("#main_item_name ^^" #> "")(ns)
      (".name *" #> item.name) (name_ns)
    } &
    //Types
    ".types" #> makeItemLinkList(Seq(item.getType)) //TODO: now only one direct type per Item
  }

  def props: NodeSeq => NodeSeq = {
    val propSets = item.getPropertySets
    val assocFieldSets = item.getAssociationFieldSets

    def makeFieldGroup(definingType: ZType): NodeSeq => NodeSeq = {
      def makePropertySet(propSet: PropertySet) = {
        def makeProperty(prop: Property) = {
          ".property_value *" #> prop.value
        }
        ".property_name *" #> propSet.propertyType.name &
        ".property *" #> propSet.getProperties.map(makeProperty(_))
      }

      def makeCompactAssocSet(assocFieldSet: AssociationFieldSet) = {
        val otherAssocFields = assocFieldSet.getAssociationFields.flatMap(_.companionAssociationFields)
        def makeRoleList(role: ZRole) = {
          ".role_type *" #> role.name &
          ".role_players" #> makeItemLinkList(otherAssocFields.filter(_.role == role).map(_.parent).toSeq.sortBy(_.zid)) //TODO sort by worth
        }
        val role = assocFieldSet.role
        val assocType = assocFieldSet.associationType
        ".association_link [href]" #> "#" &
        ".role_played *" #> role.name &
        ".association_type *" #> assocType.name &
        ".role *" #> assocFieldSet.associationFieldType.otherRoles.map(makeRoleList(_))
      }

      //makeFieldGroup return
      ".field_group_name *" #> ("Fields from " + definingType.name) &
      ".auto_property_set *" #> List.empty &
      ".property_set *" #> propSets.filter(_.definingType == definingType).map(makePropertySet(_)) &
      ".property *" #> List.empty &
      ".compact_association_set *" #> assocFieldSets.filter(_.definingType == definingType).map(makeCompactAssocSet(_)) &
      ".association_set_table *" #> List.empty
    }

    ".field_group *" #> item.getFieldDefiningTypes.toSeq.sortBy(_.zid).map(makeFieldGroup(_))
  }

  private def makeItemLinkList(items: Seq[Item]): NodeSeq => NodeSeq = {
    val intermediate = if(items.isEmpty) items else items.dropRight(1)
    ".intermediate *" #> intermediate.map{t =>
      ".listval *" #> SHtml.link(t.address,()=>(),Text(t.name))} &
    ".last *" #> items.lastOption.map{t =>
      ".listval *" #> SHtml.link(t.address,()=>(),Text(t.name))}
  }
} //end of class

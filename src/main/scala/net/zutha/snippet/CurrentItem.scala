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
      repairedZID <- ZID.repair(zid) ?~ "an invalid zid was provided"
      item <- DB.get.getItem(ZID(repairedZID)) ?~ "no item with the specifed zid can be found"
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
    ".types" #> makeItemLinkList(item.getDirectTypes.toSeq.sortBy(_.zid))
  }

  def props: NodeSeq => NodeSeq = {

    def makeFieldGroup(definingType: ItemType): NodeSeq => NodeSeq = {
      val propSets = item.getPropertySets

      def makePropertySet(propSet: PropertySet) = {
        def makeProperty(prop: Property) =
          ".property_value *" #> prop.value

        ".property_name *" #> propSet.propertyType.name &
        ".property *" #> propSet.getProperties.map(makeProperty(_))
      }

      def makeCompactAssocSet(roleType: ItemType, assocType: ItemType) =
        ".association_set_link *" #> "#" &
        ".role_played *" #> roleType.name &
        ".association_type *" #> assocType.name

      //makeFieldGroup return
      ".field_group_name *" #> ("Fields from " + definingType.name) &
      ".auto_property_set *" #> List.empty &
      ".property_set *" #> propSets.filter(_.definingItemType == definingType).map(makePropertySet(_)) &
      ".property *" #> List.empty &
      ".compact_association_set *" #> List.empty &
      ".association_set_table *" #> List.empty
    }

    ".field_group *" #> item.getFieldDefiningTypes.map(makeFieldGroup(_))
  }

  private def makeItemLinkList(items: Seq[Item]): NodeSeq => NodeSeq = {
    ".intermediate *" #> items.dropRight(1).map{
      t => ".listval *" #> SHtml.link(t.address,()=>(),Text(t.name))} &
    ".last *" #> {val t = items.last
      ".listval *" #> SHtml.link(t.address,()=>(),Text(t.name))}
  }
} //end of class

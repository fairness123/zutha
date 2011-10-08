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
      case Full(itm) => itm
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
      val definedAssocFields = assocFieldSets.filter(_.definingType == definingType)
      val definedProps = propSets.filter(_.definingType == definingType)

      def makePropertySet(propSet: PropertySet) = {
        def makeProperty(prop: Property) = {
          ".property_value *" #> prop.valueString
        }
        ".property_name *" #> propSet.propertyType.name &
        ".property *" #> propSet.properties.map(makeProperty(_))
      }

      def makeAssocSetTable(assocFieldSet: AssociationFieldSet) = {
        val otherRoles = assocFieldSet.otherRoles.toSeq.sortBy(_.name)
        val propTypes = assocFieldSet.associationType.getDefinedPropertyTypes.toSeq.sortBy(_.name)
        def makeMemberTypeList = {
          val rolesNS = otherRoles.map{r =>
            val rolePageLink = "#" //TODO implement rolePageLink
            SHtml.link(rolePageLink,()=>(),Text(r.name)):NodeSeq
          }
          val propTypesNS = propTypes.map{p => Text(p.name)}
          rolesNS++propTypesNS
        }

        def makeRow(assocField: AssociationField) = {
          ".members *" #> {
            val rolePlayersNS = otherRoles.map{role =>
              val rolePlayers = assocField.companionAssociationFields.filter(_.role == role)
                .map(_.parent).toSeq.sortBy(_.zid) //TODO sort by worth
              makeItemLinkList(rolePlayers)
            }
            val propValsNS = propTypes.map{propType =>
              val propVals: Seq[NodeSeq] = assocField.association.getProperties(propType).toSeq.map(pv => Text(pv.valueString))
              makeElemList(propVals)
            }
            rolePlayersNS++propValsNS
          }
        }
        ".association_set_name *" #> makeAssocSetHeader(assocFieldSet) &
        ".member_type *" #> makeMemberTypeList &
        ".association_row *" #> assocFieldSet.associationFields.toSeq.map(makeRow(_))
      }

      //makeFieldGroup return
      ".field_group_name *" #> ("Fields from " + definingType.name) &
      ".auto_property_set *" #> List.empty &
      ".property_set *" #> definedProps.map(makePropertySet(_)) &
      ".single_property *" #> List.empty &
      ".association_set_table *" #> definedAssocFields.map(makeAssocSetTable(_))
    }

    ".field_group *" #> item.getFieldDefiningTypes.toSeq.sortBy(_.zid).map(makeFieldGroup(_))
  }

  private def makeAssocSetHeader(assocFieldSet: AssociationFieldSet) = {
    val role = assocFieldSet.role
    val assocType = assocFieldSet.associationType
    val assocTypeName = assocType.name(role).getOrElse(assocType.name)
    val assocPageLink = "#" //TODO implement AssociationPage link
    SHtml.link(assocPageLink,()=>(),Text(assocTypeName))
  }

  private def makeElemList(elems: Seq[NodeSeq]): NodeSeq => NodeSeq = {
    val intermediate = if(elems.isEmpty) elems else elems.dropRight(1)
    ".intermediate *" #> intermediate.map{elem =>
      ".listval" #> elem} &
    ".last *" #> elems.lastOption.map{elem =>
      ".listval" #> elem}
  }
  private def makeItemLinkList(items: Seq[Item]) = {
    val elems = items.map{t => SHtml.link(t.address,()=>(),Text(t.name))}
    makeElemList(elems)
  }
} //end of class

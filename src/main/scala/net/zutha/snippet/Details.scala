package net.zutha
package snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml}
import model.constructs._
import lib.uri.{AssocLoc, RoleLoc, ItemInfo}

class Details(itemInfo: ItemInfo) {

  private val item: ZItem = itemInfo.item
  private val propSets = item.getPropertySets
  private val assocFieldSets = item.getAssociationFieldSets

  def render: NodeSeq => NodeSeq = {
    ".field_group *" #> item.getFieldDefiningTypes.toSeq.sortBy(_.zid).map(makeFieldGroup(_))
  }

  /** Render the set of fields that were defined by definingType */
  private def makeFieldGroup(definingType: ZType): NodeSeq => NodeSeq = {
    val definedAssocFields = assocFieldSets.filter(_.definingType == definingType)
    val definedProps = propSets.filter(_.definingType == definingType)

    //makeFieldGroup return
    ".field_group_name *" #> ("Fields from " + definingType.name) &
    ".auto_property_set *" #> List.empty &
    ".property_set *" #> definedProps.map(makePropertySet(_)) &
    ".single_property *" #> List.empty &
    ".association_set_table *" #> definedAssocFields.map(makeAssocSetTable(_))
  }

  /** Render all properties in a Property Set*/
  def makePropertySet(propSet: ZPropertySet) = {
    def makeProperty(prop: ZProperty) = {
      ".property_value *" #> prop.valueString
    }
    ".property_name *" #> propSet.propertyType.name &
    ".property *" #> propSet.properties.map(makeProperty(_))
  }

  /** Render a table to display all association Fields in assocFieldSet*/
  def makeAssocSetTable(assocFieldSet: ZAssociationFieldSet) = {
    val role = assocFieldSet.role
    val assocType = assocFieldSet.associationType
    val otherRoles = assocFieldSet.otherRoles.toSeq.sortBy(_.name)
    val propTypes = assocFieldSet.associationType.getDefinedPropertyTypes.toSeq.sortBy(_.name)

    def makeRow(assocField: ZAssociationField) = {
      ".roleplayers *" #> otherRoles.map{role =>
          val rolePlayers = assocField.companionAssociationFields.filter(_.role == role)
            .map(_.parent).toSeq.sortBy(_.zid) //TODO sort by worth
          SnippetUtils.makeItemLinkList(rolePlayers)
      } &
      ".prop_values *" #> propTypes.map{propType =>
          val propVals: Seq[NodeSeq] = assocField.association.getProperties(propType).toSeq.map(pv => Text(pv.valueString))
          SnippetUtils.makeElemList(propVals)
      }
    }

    ".association_set_name" #> {
      "a *" #> assocFieldSet.associationType.nameF(assocFieldSet.role) &
      "a [href]" #> AssocLoc.makeUri(item,role,assocType)
    } &
    ".role *" #> otherRoles.map{r =>
        "a *" #> r.name &
        "a [href]" #> RoleLoc.makeUri(item,role,assocType,r)
      } &
    ".prop_type *" #> propTypes.map{p => Text(p.nameF(role)):NodeSeq} &
    ".association_row *" #> assocFieldSet.associationFields.toSeq.map(makeRow(_))
  }

} //end of class

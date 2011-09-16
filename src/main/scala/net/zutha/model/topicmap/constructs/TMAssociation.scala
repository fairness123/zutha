package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constructs._
import org.tmapi.core.{Topic, Association, Role}
import net.zutha.util.Helpers._

object TMAssociation{
  val getItem = makeCache[Association,String,TMAssociation](_.getId, association => new TMAssociation(association))
  def apply(association: Association):TMAssociation = getItem(association)
}
class TMAssociation protected (association: Association) extends ZAssociation{
  def associationType = TMAssociationType(association.getType)
  def associationFields: Set[AssociationField] = association.getRoles.toSet.map(TMAssociationField(_:Role))
  def roles = association.getRoleTypes.toSet.map(TMRole(_:Topic))
  def players = association.getRoles.toSet.map((role:Role) => role.getPlayer.toItem)
  def getProperties(propType: PropertyType) = association.getReifier.getProperties(propType)
}

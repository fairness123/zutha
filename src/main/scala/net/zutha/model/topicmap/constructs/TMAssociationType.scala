package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.db.DB.db
import net.zutha.util.Helpers._
import net.zutha.model.exceptions.SchemaViolationException
import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}
import org.tmapi.core.{Role, Topic}
import net.zutha.model.constructs.{ZAssociation, ZRole, ZAssociationType}

object TMAssociationType{
  val getItem = makeCache[Topic,String,TMAssociationType](_.getId, topic => new TMAssociationType(topic))
  def apply(topic: Topic):TMAssociationType = getItem(topic)
}
class TMAssociationType protected (topic: Topic) extends TMTrait(topic) with ZAssociationType{
  def getAllSuperAssociationTypes: Set[ZAssociationType] = getAllSuperTypes.filter(_.isAssociationType).map{_.toAssociationType}

  def getDirectAssocRoleConstraints = {
    topic.getRolesPlayed(db.ASSOCIATION_TYPE,db.ASSOCIATION_ROLE_CONSTRAINT).toSet
      .map((_:Role).getParent.toZAssociation)
  }
  def getAssocRoleConstraints = {
    val allAssocRoleConstraints = getAllSuperAssociationTypes.flatMap{_.getDirectAssocRoleConstraints}
    val nonOverridden = allAssocRoleConstraints.filter(_.overriddenBy.intersect(allAssocRoleConstraints).isEmpty)
    nonOverridden
  }
  private def getAssocRoleConstraint(role: ZRole): ZAssociation = {
    //require(getAllDefinedRoles.contains(role))
    val roleDeclaringAncestors = getAssocRoleConstraints.filter(_.getRoles(db.ROLE).head.getPlayer.toRole == role)
    roleDeclaringAncestors.size match {
      case 0 => throw new SchemaViolationException("Association Type: '"+this.name+"' missing declaring ancestor for role: '"+role.name+"'")
      case 1 => roleDeclaringAncestors.toSeq.head
      case _ => throw new SchemaViolationException("role: "+role.name+" is declared more than once without being overridden")
    }
  }
  def getDirectDefinedRoles = getDirectAssocRoleConstraints.map{_.getRoles(db.ROLE).head.getPlayer.toRole}.toSet
  lazy val getAllDefinedRoles = {
    val definedRoles = getAssocRoleConstraints.map(_.getRoles(db.ROLE).head.getPlayer.toRole)
    definedRoles
  }

  //TODO find where this was supposed to be used
  def getRoleDeclaringAncestor(role: ZRole)= {
    getAssocRoleConstraint(role).getRoles(db.ASSOCIATION_TYPE).head.getPlayer.toAssociationType
  }

  def getRoleCardMin(role: ZRole) = getAssocRoleConstraint(role).getPropertyValue(db.ROLE_CARD_MIN).getOrElse(
    throw new SchemaViolationException("association-role-constraint associations must have a role-card-min property")) match {
    case value: ZNonNegativeInteger => value
    case _ => throw new SchemaViolationException("card-min properties must have datatype: ZNonNegativeInteger")
  }
  def getRoleCardMax(role: ZRole) =
    getAssocRoleConstraint(role).getPropertyValue(db.ROLE_CARD_MAX).getOrElse(
      throw new SchemaViolationException("association-role-constraint associations must have a role-card-max property")) match {
    case value: ZUnboundedNNI => value
    case _ => throw new SchemaViolationException("card-max properties must have datatype: ZUnboundedNNI")
  }

  def getDirectAssocPropertyConstraints = {
    topic.getRolesPlayed(db.ASSOCIATION_TYPE,db.ASSOCIATION_PROPERTY_CONSTRAINT).toSet
      .map((_:Role).getParent.toZAssociation)
  }
  def getAssocPropertyConstraints = {
    val allAssocPropertyConstraints = getAllSuperAssociationTypes.flatMap{_.getDirectAssocPropertyConstraints}
    val nonOverridden = allAssocPropertyConstraints.filter(_.overriddenBy.intersect(allAssocPropertyConstraints).isEmpty)
    nonOverridden
  }
  def getDirectDefinedProperties = getDirectAssocPropertyConstraints.map(_.getRoles(db.PROPERTY_TYPE).head.getPlayer.toPropertyType)
  lazy val getAllDefinedProperties = {
    val definedProps = getAssocPropertyConstraints.map(_.getRoles(db.PROPERTY_TYPE).head.getPlayer.toPropertyType)
    definedProps
  }

}
















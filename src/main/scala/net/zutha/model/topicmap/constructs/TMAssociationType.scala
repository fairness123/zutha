package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import org.tmapi.core.Topic
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.db.DB.db
import net.zutha.model.constructs.{ZRole, AssociationType}
import net.zutha.util.Helpers._
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.exceptions.SchemaViolationException
import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}

object TMAssociationType{
  val getItem = makeCache[Topic,String,TMAssociationType](_.getId, topic => new TMAssociationType(topic))
  def apply(topic: Topic):TMAssociationType = getItem(topic)
}
class TMAssociationType protected (topic: Topic) extends TMInterface(topic) with AssociationType{
  def getAllSuperAssociationTypes: Set[AssociationType] = getAllSuperTypes.map{_.toAssociationType}

  def directDefinedRoles = {
    val roleDefRoles = topic.getRolesPlayed(db.siASSOCIATION_TYPE,db.siASSOCIATION_ROLE_CONSTRAINT)
    roleDefRoles.map{_.getParent.getRoles(db.siROLE).head.getPlayer.toRole}.toSet
  }
  lazy val allDefinedRoles = { //TODO exclude overridden roles
    getAllSuperAssociationTypes.flatMap(_.directDefinedRoles)
  }

  def getRoleDeclaringAncestor(role: ZRole): AssociationType = {
    require(allDefinedRoles.contains(role))
    val roleDeclaringAncestors = getAllSuperAssociationTypes.filter(_.directDefinedRoles.contains(role))
    //TODO exclude declarers of overridden roles
    roleDeclaringAncestors.size match {
      case 0 => throw new SchemaViolationException("Association Type missing declaring ancestor for role: "+role)
      case 1 => roleDeclaringAncestors.head
      case _ => throw new SchemaViolationException("role: "+role+" is declared more than once without being overridden")
    }
  }

  def getRoleConstraintAssociation(role: ZRole) = {
    require(allDefinedRoles.contains(role))
    TopicMapDB.findAssociations(db.siASSOCIATION_ROLE_CONSTRAINT,true,
      db.siASSOCIATION_TYPE.toRole -> getRoleDeclaringAncestor(role),
      db.siROLE.toRole -> role
    ).headOption.getOrElse(
      throw new SchemaViolationException("Association Type missing association-role-constraint for "+role))
  }

  def getRoleCardMin(role: ZRole) = getRoleConstraintAssociation(role).getPropertyValue(db.siROLE_CARD_MIN).getOrElse(
    throw new SchemaViolationException("association-role-constraint associations must have a role-card-min property")) match {
    case value: ZNonNegativeInteger => value
    case _ => throw new SchemaViolationException("card-min properties must have datatype: ZNonNegativeInteger")
  }
  def getRoleCardMax(role: ZRole) = getRoleConstraintAssociation(role).getPropertyValue(db.siROLE_CARD_MAX).getOrElse(
    throw new SchemaViolationException("association-role-constraint associations must have a role-card-max property"))match {
    case value: ZUnboundedNNI => value
    case _ => throw new SchemaViolationException("card-max properties must have datatype: ZUnboundedNNI")
  }
}
















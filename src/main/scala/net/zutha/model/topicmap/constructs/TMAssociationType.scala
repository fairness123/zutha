package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import org.tmapi.core.Topic
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.db.DB.db
import net.zutha.model.constructs.{ZRole, AssociationType}
import net.zutha.util.Helpers._

object TMAssociationType{
  val getItem = makeCache[Topic,String,TMAssociationType](_.getId, topic => new TMAssociationType(topic))
  def apply(topic: Topic):TMAssociationType = getItem(topic)
}
class TMAssociationType protected (topic: Topic) extends TMInterface(topic) with AssociationType{
  def getDirectDefinedRoles = {
    val roleDefRoles = topic.getRolesPlayed(db.siASSOCIATION_TYPE,db.siASSOCIATION_ROLE_CONSTRAINT)
    roleDefRoles.map{_.getParent.getRoles(db.siROLE).head.getPlayer.toRole}.toSet
  }
  def getDefinedRoles = {
    val supertypes = getAllSuperTypes.map{_.toAssociationType} + this
    supertypes.flatMap(_.getDirectDefinedRoles)
  }

  def getRoleMinCardinality(role: ZRole) = {
    val roleDefRoles = topic.getRolesPlayed(db.siASSOCIATION_TYPE,db.siASSOCIATION_ROLE_CONSTRAINT)
    val constraintAssociation = roleDefRoles.map(_.getParent).filter(_.getRoles(db.siROLE).contains(role)).head
    val cardMinProp = constraintAssociation.getProperties(db.siROLE_CARD_MIN.toPropertyType).head
    cardMinProp.value.toInt
  }

  def getRoleMaxCardinality(role: ZRole) = {
    val assocRoleConstraints = topic.getRolesPlayed(db.siASSOCIATION_TYPE,db.siASSOCIATION_ROLE_CONSTRAINT).map(_.getParent)
    val targetAssocRoleConstraint = assocRoleConstraints
      .filter(_.getRoles(db.siROLE).map(_.getPlayer).contains(role)).head
    val cardMinProp = targetAssocRoleConstraint.getProperties(db.siROLE_CARD_MAX.toPropertyType).head
    cardMinProp.value.toInt
  }
}
















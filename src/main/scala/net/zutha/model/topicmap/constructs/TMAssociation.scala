package net.zutha.model.topicmap.constructs

import scala.collection.JavaConversions._
import net.zutha.model.topicmap.TMConversions._
import net.zutha.model.constructs._
import org.tmapi.core.{Topic, Association, Role}
import net.zutha.util.Cache._
import net.zutha.model.db.DB.db
import net.zutha.model.datatypes.PropertyValue

object TMAssociation{
  val getItem = makeCache[Association,String,TMAssociation](_.getId, association => new TMAssociation(association))
  def apply(association: Association):TMAssociation = getItem(association)
}
class TMAssociation protected (association: Association) extends ZAssociation{
  def toZAssociation: ZAssociation = this
  def toAssociation = association
  lazy val reifier = association.getReifier
  def associationType = TMAssociationType(association.getType)
  def associationFields: Set[ZAssociationField] = association.getRoles.toSet.map(TMAssociationField(_:Role))
  def playedRoles = association.getRoleTypes.map(TMRole(_:Topic)).toSet
  def players = rolePlayers.map(_._2)
  def getPlayers(role: ZRole): Set[ZItem] = rolePlayers.filter(_._1 == role).map(_._2)
  lazy val rolePlayers: Set[(ZRole,ZItem)] = association.getRolePlayersT.map{case (r,p) => (r.toRole,p.toItem)}
  def associationProperties: Set[(ZPropertyType,PropertyValue)] = {
    associationType.definedAssocProperties.flatMap{pt => getPropertyValues(pt).map(pv => (pt,pv))}
  }

  def getProperties(propType: ZPropertyType) = reifier.getProperties(propType)
  def getPropertyValues(propType: ZPropertyType) = reifier.getPropertyValues(propType)
  def getProperty(propType: ZPropertyType) = reifier.getProperty(propType)
  def getPropertyValue(propType: ZPropertyType) = reifier.getPropertyValue(propType)


  lazy val overriddenBy:Set[ZAssociation] = {
    val ovDeclRoles = association.getReifier.getRolesPlayed(db.OVERRIDDEN_DECLARATION,db.OVERRIDES_DECLARATION).toSet
    val overriders = ovDeclRoles.map(_.getParent.getRoles(db.OVERRIDING_DECLARATION).head.getPlayer
      .getReified.asInstanceOf[Association].toZAssociation)
    overriders
  }
}

package net.zutha.model.builder

import net.zutha.model.constructs._
import net.zutha.model.datatypes.PropertyValue

class AssociationFieldBuilder private[builder](val parent: ItemBuilder, val associationFieldType: ZAssociationFieldType){
  private var _rolePlayers: Set[(ZRole,ZItem,Boolean)] = Set()
  private var _properties: Set[(ZPropertyType,PropertyValue)] = Set()

  //TODO validate add/remove actions before executing them

  def rolePlayers = _rolePlayers
  def getPlayers(role:ZRole) = _rolePlayers.filter(_._1 == role).map(_._2)

  private def _addRolePlayer(role:ZRole,player:ZItem,locked:Boolean):Boolean = {
    _rolePlayers += ((role,player,locked))
    true
  }
  private[builder] def addLockedRolePlayer(rolePlayer:(ZRole,ZItem)):Boolean = {
    _addRolePlayer(rolePlayer._1,rolePlayer._2,true)
  }
  def addRolePlayer(role:ZRole,player:ZItem):Boolean = {
    _addRolePlayer(role,player,false)
  }
  def removeRolePlayer(role:ZRole,player:ZItem):Boolean = {
    _rolePlayers -= ((role,player,false))
    true
  }

  def properties = _properties
  def getPropertyValues(propType: ZPropertyType) = _properties.filter(_._1 == propType).map(_._2)

  def addProperty(propType:ZPropertyType,value:PropertyValue):Boolean = {
    _properties += ((propType,value))
    true
  }
  def removeProperty(propType:ZPropertyType,value:PropertyValue):Boolean = {
    _properties -= ((propType,value))
    true
  }

  /** Create a concrete Association item from this AssociationFieldBuilder */
  def build = {

  }
}

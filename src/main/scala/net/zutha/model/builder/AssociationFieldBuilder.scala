package net.zutha.model.builder

import net.zutha.model.constructs._
import net.zutha.model.datatypes.PropertyValue
import net.liftweb.common.Logger
import net.liftweb.util.ValueCell

class AssociationFieldBuilder private[builder](val parent: ItemBuilder, val associationFieldType: ZAssociationFieldType)
    extends FieldBuilder with Logger{
  private val _rolePlayers: ValueCell[Set[(ZRole,ZItem,Boolean)]] = ValueCell(Set())
  private val _properties: ValueCell[Set[(ZPropertyType,PropertyValue)]] = ValueCell(Set())

  //TODO validate add/remove actions before executing them

  def role = associationFieldType.role
  def otherRoles = associationFieldType.otherRoles
  
  def rolePlayers = _rolePlayers.map{ case (r,i,l) => (r,i) }
  def getPlayers(role:ZRole) = _rolePlayers.filter(_._1 == role).map(_._2)

  def allowedPlayers(role:ZRole): Set[ZItem] = {
    val allowed = associationFieldType.allowedPlayersOf(role)
    allowed
  }

  private def _addRolePlayer( role: ZRole, player: ZItem, locked: Boolean ): Boolean = {
    debug("Before adding Role Player: rolePlayers = " + _rolePlayers)
    _rolePlayers.atomicUpdate{_ + ((role,player,locked))}
    debug("After adding Role Player: rolePlayers = " + _rolePlayers)
    true
  }
  private[builder] def addLockedRolePlayer( rolePlayer: (ZRole,ZItem) ): Boolean = {
    _addRolePlayer(rolePlayer._1,rolePlayer._2,true)
  }
  def addRolePlayer( role: ZRole, player: ZItem ): Boolean = {
    _addRolePlayer(role,player,false)
  }
  def removeRolePlayer( role: ZRole, player: ZItem ): Boolean = {
    debug("Before removing Role Player: rolePlayers = " + _rolePlayers)
     _rolePlayers.atomicUpdate{_ - ((role,player,false))}
    debug("After removing Role Player: rolePlayers = " + _rolePlayers)
    true
  }

  def properties = _properties
  def getPropertyValues( propType: ZPropertyType ) = _properties.filter(p => p._1 == propType).map(_._2)

  def addProperty( propType: ZPropertyType, value: PropertyValue ): Boolean = {
    debug(_properties)
    _properties.atomicUpdate{_ + ((propType,value))}
    debug(_properties)
    true
  }
  def removeProperty( propType: ZPropertyType, value: PropertyValue ): Boolean = {
    _properties.atomicUpdate{_ - ((propType,value))}
    true
  }

  /** Create a concrete Association item from this AssociationFieldBuilder */
  def build = {

  }
}

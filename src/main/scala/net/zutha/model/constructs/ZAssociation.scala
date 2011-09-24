package net.zutha.model.constructs

import net.zutha.model.datatypes.{PropertyValue}

trait ZAssociation {
  def getAssociationType: AssociationType
  def getAssociationFields: Set[AssociationField]
  def getRoles: Set[ZRole]
  def getAllPlayers: Set[Item]
  def getRolePlayers: Set[(ZRole,Item)]
  def getPlayers(role: ZRole): Set[Item]
  def getProperties(propType: PropertyType): Set[Property]
  def getPropertyValues(propType: PropertyType): Set[PropertyValue]
  def getProperty(propType: PropertyType):Option[Property]
  def getPropertyValue(propType: PropertyType): Option[PropertyValue]
}

package net.zutha.model.constructs

import net.zutha.model.datatypes.{PropertyValue}

trait ZAssociation {
  def getAssociationType: AssociationType
  def getAssociationFields: Set[AssociationField]
  def getPlayedRoles: Set[ZRole]
  def getAllPlayers: Set[Item]
  def getRolePlayers: Set[(ZRole,Item)]
  def getPlayers(role: ZRole): Set[Item]
  def getProperties(propType: PropertyType): Set[Property]
  def getPropertyValues(propType: PropertyType): Set[PropertyValue]
  def getProperty(propType: PropertyType):Option[Property]
  def getPropertyValue(propType: PropertyType): Option[PropertyValue]

  /** @return the schema associations that override this one.
   *  Will always return an empty set for any associations that cannot be overridden. */
  def overriddenBy:Set[ZAssociation]
}

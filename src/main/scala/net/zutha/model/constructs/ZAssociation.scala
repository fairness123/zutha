package net.zutha.model.constructs

trait ZAssociation {
  def associationType: AssociationType
  def associationFields: Set[AssociationField]
  def roles: Set[ZRole]
  def players: Set[Item]
  def getProperties(propType: PropertyType): Set[Property]
}

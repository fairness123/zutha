package net.zutha.model.constructs


trait AssociationFieldSet {
  def parentItem: Item
  def definingType: ZType
  def associationFieldType: AssociationFieldType
  def role: ZRole
  def associationType: AssociationType
  def getAssociationFields: Set[AssociationField]
}

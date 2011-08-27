package net.zutha.model.constructs


trait AssociationFieldSet extends FieldSet {
  def roleType: ItemType
  def associationType: ItemType
  def getAssociations: Set[Association]
}

package net.zutha.model.constructs

trait AssociationField {
  def parent: Item
  def role: ZRole
  def association: ZAssociation
  def companionAssociationFields: Set[AssociationField] = association.associationFields - this
  def otherPlayers: Set[Item] = association.players - parent
}

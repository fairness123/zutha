package net.zutha.model.constructs

trait AssociationField {
  def parent: Item
  def role: ZRole
  def association: ZAssociation
  def companionAssociationFields: Set[AssociationField] = association.getAssociationFields - this
  def otherPlayers: Set[Item] = association.getAllPlayers - parent
  def otherRoles: Set[ZRole] = companionAssociationFields.map(_.role)
}

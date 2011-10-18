package net.zutha.model.constructs

trait ZAssociationField {
  def parent: ZItem
  def role: ZRole
  def association: ZAssociation
  def companionAssociationFields: Set[ZAssociationField] = association.getAssociationFields - this
  def otherPlayers: Set[ZItem] = association.getAllPlayers - parent
  def otherRoles: Set[ZRole] = companionAssociationFields.map(_.role)
}

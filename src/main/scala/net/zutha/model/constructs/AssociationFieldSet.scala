package net.zutha.model.constructs

import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}

trait AssociationFieldSet {
  def parentItem: Item
  def definingType: ZType
  def associationFieldType: AssociationFieldType
  def otherAssociationFieldTypes: Set[AssociationFieldType] = associationFieldType.companionAssociationFieldTypes
  def role: ZRole
  def otherRoles: Set[ZRole] = associationFieldType.otherRoles
  def associationType: AssociationType
  def associationFields: Set[AssociationField]
  def cardMin: ZNonNegativeInteger
  def cardMax: ZUnboundedNNI
}

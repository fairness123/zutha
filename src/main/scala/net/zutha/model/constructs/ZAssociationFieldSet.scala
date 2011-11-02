package net.zutha.model.constructs

import net.zutha.model.datatypes.{ZNonNegativeInteger, ZUnboundedNNI}

trait ZAssociationFieldSet {
  def parentItem: ZItem
  def definingType: ZType
  def associationFieldType: ZAssociationFieldType
  def otherAssociationFieldTypes: Set[ZAssociationFieldType] = associationFieldType.companionAssociationFieldTypes
  def role: ZRole
  def otherRoles: Set[ZRole] = associationFieldType.otherRoles
  def propertyTypes: Set[ZPropertyType] = associationType.getDefinedPropertyTypes
  def associationType: ZAssociationType
  def associationFields: Set[ZAssociationField]
  def isEmpty: Boolean
  def cardMin: ZNonNegativeInteger
  def cardMax: ZUnboundedNNI
}

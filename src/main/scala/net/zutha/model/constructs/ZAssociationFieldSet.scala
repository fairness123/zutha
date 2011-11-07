package net.zutha.model.constructs

import net.zutha.model.db.DB._
import net.zutha.model.exceptions.SchemaViolationException

object ZAssociationFieldSet{
  def apply(parentItem: ZItem, assocFT:ZAssociationFieldType): ZAssociationFieldSet =
    ZAssociationFieldSet(parentItem,assocFT.role,assocFT.associationType)
  def apply(parentItem: ZItem, assocFST:ZAssociationFieldSetType): ZAssociationFieldSet =
    ZAssociationFieldSet(parentItem,assocFST.role,assocFST.associationType)
}

case class ZAssociationFieldSet(parentItem: ZItem, role: ZRole, associationType: ZAssociationType){

  def associationFieldSetType = {
    val declaringType = associationFieldType.declarerForItem(parentItem) match{
      case None => throw new SchemaViolationException(this + " has no non-overridden association-field-declarations")
      case Some(t) => t
    }
    ZAssociationFieldSetType(declaringType,role,associationType)
  }

  def associationFieldType = ZAssociationFieldType(role,associationType)
  def associationFields = parentItem.getAssociationFields(associationFieldType)
  def otherAssociationFieldTypes: Set[ZAssociationFieldType] = associationFieldType.companionAssociationFieldTypes
  def otherRoles: Set[ZRole] = associationFieldType.otherRoles
  def propertyTypes: Set[ZPropertyType] = associationType.definedAssocProperties

  def isEmpty = associationFields.isEmpty
  def cardMin = associationFieldSetType.cardMin
  def cardMax = associationFieldSetType.cardMax
}

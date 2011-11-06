package net.zutha.model.constructs

object ZAssociationFieldSet{
  def apply(parentItem: ZItem, definingType: ZType, assocFT:ZAssociationFieldType): ZAssociationFieldSet =
    ZAssociationFieldSet(parentItem,definingType,assocFT.role,assocFT.associationType)
  def apply(parentItem: ZItem, assocFST:ZAssociationFieldSetType): ZAssociationFieldSet =
    ZAssociationFieldSet(parentItem,assocFST.definingType,assocFST.role,assocFST.associationType)
}

case class ZAssociationFieldSet(parentItem: ZItem, definingType: ZType,
                                 role: ZRole, associationType: ZAssociationType){

  def associationFieldSetType = ZAssociationFieldSetType(definingType,role,associationType)
  def associationFieldType = ZAssociationFieldType(role,associationType)
  def associationFields = parentItem.getAssociationFields(associationFieldType)
  def otherAssociationFieldTypes: Set[ZAssociationFieldType] = associationFieldType.companionAssociationFieldTypes
  def otherRoles: Set[ZRole] = associationFieldType.otherRoles
  def propertyTypes: Set[ZPropertyType] = associationType.definedAssocProperties

  def isEmpty = associationFields.isEmpty
  def cardMin = associationFieldSetType.cardMin
  def cardMax = associationFieldSetType.cardMax
}

package net.zutha.model.constructs


trait ZType extends ZItem{
  //type information
  def isAbstract: Boolean;
  def hasSuperType(superType: ZType): Boolean
  
  /** @return the set of all this type's supertypes (including this type itself)*/
  def getAllSuperTypes: Set[ZType]

  // field definition
  def declaresFields: Boolean;
  def declaredPropertyTypes: Set[ZPropertyType];
  def declaredAssociationFieldTypes: Set[ZAssociationFieldType];
  def requiredPropertyTypes: Set[ZPropertyType];
  def requiredAssociationFieldTypes: Set[ZAssociationFieldType];
}

package net.zutha.model.constructs


trait ZType extends ZItem{
  //type information
  def isAbstract: Boolean;
  def hasSuperType(superType: ZType): Boolean
  
  /** @return the set of all this type's supertypes (including this type itself)*/
  def getAllSuperTypes: Set[ZType]

  // field definition
  def definesFields: Boolean;
//  def getDefinedFields: Seq[ZItemType];
  def getDefinedPropertyTypes: Set[ZPropertyType];
  def getDefinedAssociationFieldTypes: Set[ZAssociationFieldType];
}

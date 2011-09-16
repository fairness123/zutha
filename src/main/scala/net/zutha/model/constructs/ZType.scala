package net.zutha.model.constructs


trait ZType extends Item{
  //type information
  def isAbstract: Boolean;
  def hasSuperType(superType: ZType): Boolean
  def getAllSuperTypes: Set[ZType]

  // field definition
  def definesFields: Boolean;
//  def getDefinedFields: Seq[ItemType];
  def getDefinedProperties: Set[PropertyType];
  def getDefinedAssociationFields: Set[AssociationFieldType];
}

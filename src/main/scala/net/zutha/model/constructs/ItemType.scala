package net.zutha.model.constructs

trait ItemType extends Item {
  //type information
  def isAbstract: Boolean;
  def hasSuperType(superType: ItemType): Boolean
  def getAllSuperTypes: Seq[ItemType];

  // field definition
  def definesFields: Boolean;
//  def getDefinedFields: Seq[ItemType];
  def getDefinedProperties: Seq[ItemType];
  def getDefinedAssociationFields: Seq[(Item,ItemType)];
}

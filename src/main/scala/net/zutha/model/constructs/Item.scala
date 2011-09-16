package net.zutha.model.constructs

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

trait Item {
  // -------------- conversion --------------
  def isZType: Boolean
  def toZType: ZType

  def isItemType: Boolean
  def toItemType: ItemType

  def isInterface: Boolean
  def toInterface: Interface

  def isAssociationType: Boolean
  def toAssociationType: AssociationType

  def isPropertyType: Boolean
  def toPropertyType: PropertyType

  def isRole: Boolean
  def toRole: ZRole

  // -------------- ZIDs --------------
  def zid: String;
  def getZIDs: Set[String];
  def addZID(zid: Zid);

  // -------------- names --------------
  /**@return this item's first name in the unconstrained scope */
  def name: String;

  // -------------- types --------------
  def hasType(itemType: ZType): Boolean;
  def getType: ZType;
  def getAllTypes: Set[ZType];
  def getFieldDefiningTypes: Set[ZType];

  // -------------- fields --------------
  def getPropertySets: Set[PropertySet]
  def getProperties(propType: PropertyType): Set[Property];
  def getAssociationFieldSets: Set[AssociationFieldSet]
  def getAssociationFields(role: ZRole, assocType: AssociationType): Set[AssociationField]

  // -------------- zutha.net-specific properties --------------

  // web address of form: /item/<zid>/<name>
  def address: String;
}



package net.zutha.model.constructs

import net.zutha.model.datatypes.{PropertyValue, DataType}

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
  /** @return a set of names having the given scope */
  def names(scope: ZScope):Set[String]
  /** @return a set of names having the scope specified by the given list of Items*/
  def names(scopeItems: Item*):Set[String]
  /** @return all names in any scope*/
  def allNames:Set[String]
  /** @return all names in the unconstrained scope*/
  def unconstrainedNames:Set[String]
  /** @return this item's primary name in the given scope or None*/
  def name(scope: ZScope): Option[String]
  /** @return this item's primary name in the given scope or None*/
  def name(scopeItems: Item*): Option[String]
  /** @return this item's primary name in the unconstrained scope */
  def name: String;

  // -------------- types --------------
  def hasType(itemType: ZType): Boolean;
  def getType: ZType;
  def getAllTypes: Set[ZType];
  def getFieldDefiningTypes: Set[ZType];

  // -------------- fields --------------
  def getPropertySets: Set[PropertySet]
  def getProperties(propType: PropertyType): Set[Property]
  def getPropertyValues(propType: PropertyType): Set[PropertyValue]
  def getProperty(propType: PropertyType): Option[Property]
  def getPropertyValue(propType: PropertyType): Option[PropertyValue]

  def getAssociationFieldSets: Set[AssociationFieldSet]
  def getAssociationFields(assocFieldType: AssociationFieldType): Set[AssociationField]

  // -------------- zutha.net-specific properties --------------

  // web address of form: /item/<zid>/<name>
  def address: String;
}



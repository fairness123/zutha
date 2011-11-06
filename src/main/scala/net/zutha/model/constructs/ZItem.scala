package net.zutha.model.constructs

import net.zutha.model.datatypes.{PropertyValue, DataType}

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

trait ZItem {
  // -------------- conversion --------------
  def isType: Boolean
  def toType: ZType

  def isItemType: Boolean
  def toItemType: ZItemType

  def isTrait: Boolean
  def toTrait: ZTrait

  def isAssociationType: Boolean
  def toAssociationType: ZAssociationType

  def isPropertyType: Boolean
  def toPropertyType: ZPropertyType

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
  def names(scopeItems: ZItem*):Set[String]
  /** @return all names in any scope*/
  def allNames:Set[String]
  /** @return all names in the unconstrained scope*/
  def unconstrainedNames:Set[String]
  /** @return this item's primary name in the given scope or None*/
  def name(scope: ZScope): Option[String]
  /** @return this item's primary name in the given scope or None*/
  def name(scopeItems: ZItem*): Option[String]
  /** @return this item's primary name in the given scope if it exists,
   * otherwise return the primary name in the Unconstrained Scope */
  def nameF(scope: ZScope): String = name(scope).getOrElse(name)
  /** @return this item's primary name in the given scope if it exists,
   * otherwise return the primary name in the Unconstrained Scope */
  def nameF(scopeItems: ZItem*): String = name(scopeItems:_*).getOrElse(name)
  /** @return this item's primary name in the unconstrained scope */
  def name: String;

  // -------------- types --------------
  def hasType(itemType: ZType): Boolean;
  def getType: ZType;
  def getAllTypes: Set[ZType];
  def getFieldDefiningTypes: Set[ZType];

  // -------------- fields --------------
  def getPropertySets: Set[ZPropertySet]
  def getPropertySetsGrouped: Map[ZType,Set[ZPropertySet]]
  def getNonEmptyPropertySetsGrouped: Map[ZType,Set[ZPropertySet]]
  def getProperties(propType: ZPropertyType): Set[ZProperty]
  def getPropertyValues(propType: ZPropertyType): Set[PropertyValue]
  def getProperty(propType: ZPropertyType): Option[ZProperty]
  def getPropertyValue(propType: ZPropertyType): Option[PropertyValue]

  def getAssociationFieldSets: Set[ZAssociationFieldSet]
  def getAssociationFieldSetsGrouped: Map[ZType,Set[ZAssociationFieldSet]]
  def getNonEmptyAssociationFieldSetsGrouped: Map[ZType,Set[ZAssociationFieldSet]]
  def getAssociationFieldSet(role: ZRole, assocType: ZAssociationType): Option[ZAssociationFieldSet]
  def getAssociationFields(assocFieldType: ZAssociationFieldType): Set[ZAssociationField]
  def getAssociationFields(role: ZRole, assocType: ZAssociationType): Set[ZAssociationField]
}



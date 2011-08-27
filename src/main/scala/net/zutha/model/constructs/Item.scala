package net.zutha.model.constructs

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

trait Item {
  // conversion
  def toItemType: ItemType;
  def isItemType: Boolean;
  
  // ZIDs
  def zid: String;
  def getZIDs: Seq[String];
  def addZID(zid: ZID);

  // names
  def name: String;

  // web address of form: /item/<zid>/<name>
  def address: String;

  // types
  def hasType(itemType: ItemType): Boolean;
  def getDirectTypes: Set[ItemType];
  def getAllTypes: Set[ItemType];
  def getFieldDefiningTypes: Set[ItemType];

  // fields
  def getPropertySets: Set[PropertySet]
  def getProperties(propType: ItemType): Set[Property];
}



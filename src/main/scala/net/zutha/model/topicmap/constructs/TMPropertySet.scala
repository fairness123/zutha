package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

class TMPropertySet(parent: Item, propType: ItemType, definingType: ItemType) extends PropertySet{

  def parentItem = parent
  def propertyType = propType
  def definingItemType = definingType

  def getProperties = parent.getProperties(propType)
}

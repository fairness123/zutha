package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

case class TMPropertySet(parentItem: Item, propertyType: PropertyType, definingType: ZType) extends PropertySet{
  def properties = parentItem.getProperties(propertyType)

  def cardMin = 0

  def cardMax = 0
}

package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

case class TMPropertySet(parentItem: Item, propertyType: PropertyType, definingType: ZType) extends PropertySet{
  def getProperties = parentItem.getProperties(propertyType)


}

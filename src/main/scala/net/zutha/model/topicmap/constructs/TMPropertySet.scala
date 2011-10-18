package net.zutha.model.topicmap.constructs

import net.zutha.model.constructs._

case class TMPropertySet(parentItem: ZItem, propertyType: ZPropertyType, definingType: ZType) extends ZPropertySet{
  def properties = parentItem.getProperties(propertyType)

  //TODO implement PropertySet cardMin, cardMax
  def cardMin = 0

  def cardMax = 0
}

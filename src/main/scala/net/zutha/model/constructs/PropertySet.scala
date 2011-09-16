package net.zutha.model.constructs


trait PropertySet{
  def parentItem: Item
  def definingType: ZType
  def propertyType: PropertyType
  def getProperties: Set[Property]
}

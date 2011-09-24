package net.zutha.model.constructs


trait PropertySet{
  def parentItem: Item
  def definingType: ZType
  def propertyType: PropertyType
  def properties: Set[Property]
  def cardMin: Int
  def cardMax: Int
}

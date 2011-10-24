package net.zutha.model.constructs


trait ZPropertySet{
  def parentItem: ZItem
  def definingType: ZType
  def propertyType: ZPropertyType
  def properties: Set[ZProperty]
  def isEmpty: Boolean
  def cardMin: Int
  def cardMax: Int
}

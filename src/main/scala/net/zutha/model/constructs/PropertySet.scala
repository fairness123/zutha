package net.zutha.model.constructs


trait PropertySet extends FieldSet{
  def propertyType: ItemType
  def getProperties: Set[Property]
}

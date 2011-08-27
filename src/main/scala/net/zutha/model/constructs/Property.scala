package net.zutha.model.constructs

trait Property {
  def toProperty: Property;
  def propertyType: ItemType;
  def value: String
  def datatype: String
}

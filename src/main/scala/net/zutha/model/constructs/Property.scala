package net.zutha.model.constructs

import net.zutha.model.datatypes.{PropertyValue, DataType}

trait Property {
  def toProperty: Property;
  def propertyType: PropertyType;
  def valueString: String
  def value: PropertyValue
  def dataTypeItem: Item
  def dataType: DataType
}

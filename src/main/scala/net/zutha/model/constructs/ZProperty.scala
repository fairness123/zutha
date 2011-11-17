package net.zutha.model.constructs

import net.zutha.model.datatypes.{PropertyValue, DataType}

trait ZProperty extends ZField{
  def toProperty: ZProperty;
  def propertyType: ZPropertyType;
  def valueString: String
  def value: PropertyValue
  def dataTypeItem: ZItem
  def dataType: DataType
}

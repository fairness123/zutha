package net.zutha.model.constructs

import net.zutha.model.datatypes.{DataType}

trait PropertyType extends Interface{
  def dataTypeItem: Item
  def dataType: DataType
}

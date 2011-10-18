package net.zutha.model.constructs

import net.zutha.model.datatypes.{DataType}

trait ZPropertyType extends ZInterface{
  def dataTypeItem: ZItem
  def dataType: DataType
}

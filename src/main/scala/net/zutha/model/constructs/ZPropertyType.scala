package net.zutha.model.constructs

import net.zutha.model.datatypes.{DataType}

trait ZPropertyType extends ZTrait{
  def dataTypeItem: ZItem
  def dataType: DataType
}

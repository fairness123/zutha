package net.zutha.model.datatypes

import net.zutha.model.db.DB.db
import net.zutha.model.constructs.Item

object DataType{
  private val dataTypeMap = Map[Item,DataType](
    db.siNonNegativeInteger -> ZNonNegativeInteger,
    db.siUnboundedNonNegativeInteger -> ZUnboundedNNI
  )
  
  def apply(dataTypeItem: Item): DataType = {
    dataTypeMap(dataTypeItem)
  }
}
trait DataType {
  def apply(value: String): Option[PropertyValue]
}

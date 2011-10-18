package net.zutha.model.datatypes

import net.zutha.model.db.DB.db
import net.zutha.model.constructs.ZItem

object DataType{
  private val dataTypeMap = Map[ZItem,DataType](
    db.siNonNegativeInteger -> ZNonNegativeInteger,
    db.siUnboundedNonNegativeInteger -> ZUnboundedNNI
  )
  
  def apply(dataTypeItem: ZItem): DataType = {
    dataTypeMap(dataTypeItem)
  }
}
trait DataType {
  def apply(value: String): Option[PropertyValue]
}

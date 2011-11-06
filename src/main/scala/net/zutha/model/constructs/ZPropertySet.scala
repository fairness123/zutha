package net.zutha.model.constructs

object ZPropertySet {
  def apply(parentItem: ZItem, propSetType:ZPropertySetType): ZPropertySet =
    ZPropertySet(parentItem, propSetType.definingType, propSetType.propertyType)
}
case class ZPropertySet(parentItem: ZItem, definingType: ZType, propertyType: ZPropertyType){
  def propertySetType = ZPropertySetType(definingType,propertyType)
  def properties = parentItem.getProperties(propertyType)

  def isEmpty = properties.isEmpty

  def cardMin = propertySetType.cardMin
  def cardMax = propertySetType.cardMax
}

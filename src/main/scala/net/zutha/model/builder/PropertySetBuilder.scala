package net.zutha.model.builder

import net.zutha.model.constructs._
import net.zutha.model.datatypes.ZUnboundedNNI._

class PropertySetBuilder(val parent: ItemBuilder, propSetType: ZPropertySetType) {
  val propType = propSetType.propertyType
  private var _properties: Set[PropertyBuilder] = Set()

  /**
   * @return Some(new Property) if a new one is allowed
   *  or None if this PropertySet is not allowed to have more members
   */
  def addProperty: Option[PropertyBuilder] = {
    if(_properties.size < propSetType.cardMax){
      val newBuilder = new PropertyBuilder(parent,propType)
      _properties += newBuilder
      Some(newBuilder)
    }
    else None

  }

  /**
   * Removes the given Property from this PropertySet
   * @returns true if it was removed successfully or if it was not found
   *  and false if this PropertySet is not allowed to have less members
   */
  def removeProperty(toRemove:PropertyBuilder):Boolean = {
    if(_properties.size > propSetType.cardMin){
      _properties -= toRemove
      true
    }
    else false
  }
}

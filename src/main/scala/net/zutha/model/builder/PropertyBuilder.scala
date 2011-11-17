package net.zutha.model.builder

import net.zutha.model.datatypes.{PropertyValue, ZPermissionLevel}
import net.zutha.model.constructs.{ZScope, ZItem, ZPropertyType}


class PropertyBuilder private[builder](val parent: ItemBuilder, propType: ZPropertyType)
    extends FieldBuilder{
  private val datatype = propType.dataType
  private var _value: PropertyValue = propType.dataType.default
  private var _scope: ZScope = ZScope()

  def value = _value

  /**Sets the value of this property.
   * @param value the new value to set the property to in serialized string form
   * @throws IllegalArgumentException if the given value is not valid for this property's type
   */
  def value_= (value:String) {
    value match {
      case datatype(propVal) => _value = propVal
      case _ => throw new IllegalArgumentException(value+" is not a valid value for properties of type: " + propType)
    }
  }
  
  def value_= (value: PropertyValue) {
    value match {
      case datatype(propVal) => _value = propVal
      case _ => throw new IllegalArgumentException(value+" is not a valid value for properties of type: " + propType)
    }
  }

  def scope = _scope
  def scope_= (scope:ZScope) {_scope=scope}
  def addScopeItem(scopeItem:ZItem) {_scope = new ZScope(_scope.scopeItems + scopeItem)}
  def removeScopeItem(scopeItem:ZItem) {_scope = new ZScope(_scope.scopeItems - scopeItem)}

  private var _permissionLevel: ZPermissionLevel = ZPermissionLevel.Inherit
  def permissionLevel = _permissionLevel
  def permissionLevel_= (level: Int) {_permissionLevel = ZPermissionLevel(level)}
}

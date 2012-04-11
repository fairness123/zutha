package net.zutha.model.builder

import net.zutha.model.datatypes.{PropertyValue, ZFieldLock}
import net.zutha.model.topicmap.db.TopicMapDB
import net.zutha.model.constructs.{ZProperty, ZScope, ZItem, ZPropertyType}
import org.tmapi.core.Topic
import net.zutha.model.topicmap.constructs.TMOccurrenceProperty
import net.zutha.model.db.DB.db
import net.zutha.model.topicmap.TMConversions._

class PropertyBuilder private[builder](val parent: ItemBuilder, val propertyType: ZPropertyType)
    extends FieldBuilder{
  private val datatype = propertyType.dataType
  private var _value: PropertyValue = propertyType.dataType.default
  private var _scope: ZScope = ZScope()

  def value = _value

  /**Sets the value of this property.
   * @param value the new value to set the property to in serialized string form
   * @throws IllegalArgumentException if the given value is not valid for this property's type
   */
  def value_= (value:String) {
    value match {
      case datatype(propVal) => _value = propVal
      case _ => throw new IllegalArgumentException(value+" is not a valid value for properties of type: " + propertyType)
    }
  }
  
  def value_= (value: PropertyValue) {
    value match {
      case datatype(propVal) => _value = propVal
      case _ => throw new IllegalArgumentException(value+" is not a valid value for properties of type: " + propertyType)
    }
  }

  def scope = _scope
  def scope_= (scope:ZScope) {_scope=scope}
  def addScopeItem(scopeItem:ZItem) {_scope = new ZScope(_scope.scopeItems + scopeItem)}
  def removeScopeItem(scopeItem:ZItem) {_scope = new ZScope(_scope.scopeItems - scopeItem)}

  private[builder] def build(parentTopic: Topic): ZProperty = {
    val propTypeTopic = propertyType
    val propValue = value.toString
    val occ = parentTopic.createOccurrence(propTypeTopic,propValue)

    val propTopic = TopicMapDB.createTopic(propTypeTopic)
    occ.setReifier(propTopic)

    TMOccurrenceProperty(occ)
  }
}

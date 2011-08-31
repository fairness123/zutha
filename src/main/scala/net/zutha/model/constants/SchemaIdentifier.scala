package net.zutha.model.constants
import ZuthaConstants._

object SchemaIdentifier extends Enumeration {
  type SchemaIdentifier = Value

  //types of item types
  val ITEM_TYPE = Value(ZSI_PREFIX + "item-type")
  val FIELD_TYPE = Value(ZSI_PREFIX + "field-type")
  val PROPERTY_TYPE = Value(ZSI_PREFIX + "property-type")
  val ASSOCIATION_TYPE = Value(ZSI_PREFIX + "association-type")

  //item types
  val ITEM = Value(ZSI_PREFIX + "item")
  val ROLE = Value(ZSI_PREFIX + "role")

  //association types
  val FIELD_DECLARATION = Value(ZSI_PREFIX + "field-declaration")
  val PROPERTY_DECLARATION = Value(ZSI_PREFIX + "property-declaration")
  val ASSOCIATION_FIELD_DECLARATION = Value(ZSI_PREFIX + "association-field-declaration")
  val ASSOCIATION_ROLE_CONSTRAINT = Value(ZSI_PREFIX + "association-role-constraint")
  val ABSTRACT_CONSTRAINT = Value(ZSI_PREFIX + "abstract-constraint")

  //role types
  val FIELD_DECLARER = Value(ZSI_PREFIX + "field-declarer")
  val PROPERTY_DECLARER = Value(ZSI_PREFIX + "property-declarer")
  val ASSOCIATION_FIELD_DECLARER = Value(ZSI_PREFIX + "association-field-declarer")

  //property types
  val NAME = Value(ZSI_PREFIX + "name")
  val MODIFIABLE_NAME = Value(ZSI_PREFIX + "modifiable-name")
  val ZID = Value(ZSI_PREFIX + "zid")

  // Topic Map implementation Constructs
  val ANONYMOUS_TOPIC = Value(ZTM_PREFIX + "anonymous-topic")
  val REIFIED_ZDM_ASSOCIATION = Value(ZTM_PREFIX + "reified-zdm-association")
  val ANONYMOUS_TOPIC_LINK = Value(ZTM_PREFIX + "anonymous-topic-link")

}

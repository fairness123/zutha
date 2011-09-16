package net.zutha.model.constants
import ZuthaConstants._

object SchemaIdentifier extends Enumeration {
  type SchemaIdentifier = Value
  val ZSI = ZSI_PREFIX

  //types of item types
  val TYPE = Value("type")
  val ITEM_TYPE = Value("item-type")
  val INTERFACE = Value("interface")
  val FIELD_TYPE = Value("field-type")
  val PROPERTY_TYPE = Value("property-type")
  val ASSOCIATION_TYPE = Value("association-type")

  //item types
  val ITEM = Value("item")
  val ROLE = Value("role")

  //association types
  val FIELD_DECLARATION = Value("field-declaration")
  val PROPERTY_DECLARATION = Value("property-declaration")
  val ASSOCIATION_FIELD_DECLARATION = Value("association-field-declaration")
  val ASSOCIATION_ROLE_CONSTRAINT = Value("association-role-constraint")
  val ABSTRACT_CONSTRAINT = Value("abstract-constraint")

  //role types
  val FIELD_DECLARER = Value("field-declarer")
  val PROPERTY_DECLARER = Value("property-declarer")
  val ASSOCIATION_FIELD_DECLARER = Value("association-field-declarer")

  //property types
  val ROLE_CARD_MIN = Value("role-card-min")
  val ROLE_CARD_MAX = Value("role-card-max")
  
  val NAME = Value("name")
  val MODIFIABLE_NAME = Value("modifiable-name")
  val ZID = Value("zid")

  // Topic Map implementation Constructs
  val ANONYMOUS_TOPIC = Value("topicmap/anonymous-topic")
  val REIFIED_ZDM_ASSOCIATION = Value("topicmap/reified-zdm-association")
  val ANONYMOUS_TOPIC_LINK = Value("topicmap/anonymous-topic-link")

}
